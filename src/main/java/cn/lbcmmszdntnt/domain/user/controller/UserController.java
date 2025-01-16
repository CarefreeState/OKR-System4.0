package cn.lbcmmszdntnt.domain.user.controller;

import cn.lbcmmszdntnt.common.SystemJsonResponse;
import cn.lbcmmszdntnt.domain.qrcode.service.QRCodeService;
import cn.lbcmmszdntnt.domain.user.enums.UserType;
import cn.lbcmmszdntnt.domain.user.model.converter.UserConverter;
import cn.lbcmmszdntnt.domain.user.model.dto.EmailBindingDTO;
import cn.lbcmmszdntnt.domain.user.model.dto.UserinfoDTO;
import cn.lbcmmszdntnt.domain.user.model.dto.WxBindingDTO;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.domain.user.model.vo.UserTypeVO;
import cn.lbcmmszdntnt.domain.user.model.vo.UserVO;
import cn.lbcmmszdntnt.domain.user.service.UserPhotoService;
import cn.lbcmmszdntnt.domain.user.service.UserService;
import cn.lbcmmszdntnt.domain.user.service.WxBindingService;
import cn.lbcmmszdntnt.interceptor.annotation.Intercept;
import cn.lbcmmszdntnt.interceptor.context.InterceptorContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-20
 * Time: 0:07
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Intercept(permit = {UserType.NORMAL_USER, UserType.MANAGER})
@Validated
public class UserController {

    private final UserService userService;

    private final QRCodeService QRCodeService;

    private final UserPhotoService userPhotoService;

    private final WxBindingService wxBindingService;

    @PostMapping("/check/wx")
    @Operation(summary = "获取微信绑定码")
    @Tag(name = "用户测试接口/微信")
    public SystemJsonResponse<String> wxIdentifyCheck() {
        Long userId = InterceptorContext.getUser().getId();
        // 生成一个小程序检查码
        String mapPath = QRCodeService.getBindingQRCode(userId, wxBindingService.getSecret(userId));
        return SystemJsonResponse.SYSTEM_SUCCESS(mapPath);
    }

    @PostMapping("/binding/email")
    @Operation(summary = "绑定用户邮箱")
    @Tag(name = "用户测试接口/邮箱")
    public SystemJsonResponse<?> emailBinding(@Valid @RequestBody EmailBindingDTO emailBindingDTO) {
        String email = emailBindingDTO.getEmail();
        String code = emailBindingDTO.getCode();
        // 获取当前登录的用户
        // todo: 考察是否要限制绑定次数，或者是否可以重新绑定，当前不做限制
        User userRecord = InterceptorContext.getUser();
        Long userId = userRecord.getId();
        String recordEmail = userRecord.getEmail();
        userService.bindingEmail(userId, email, code, recordEmail);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @PostMapping("/binding/wx")
    @Operation(summary = "绑定用户微信")
    @Tag(name = "用户测试接口/微信")
    @Intercept(authenticate = false, authorize = false)
    public SystemJsonResponse<?> wxBinding(@Valid @RequestBody WxBindingDTO wxBindingDTO) {
        Long userId = wxBindingDTO.getUserId();
        String randomCode = wxBindingDTO.getRandomCode();
        String code = wxBindingDTO.getCode();
        userService.bindingWx(userId, randomCode, code);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @PostMapping(value = "/photo/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传用户头像")
    @Tag(name = "用户测试接口/信息")
    public SystemJsonResponse<String> uploadPhoto(@Parameter(description = "用户头像（只能上传图片）") @NotNull(message = "用户头像不能为空") @RequestPart("photo") MultipartFile multipartFile) throws IOException {
        User user = InterceptorContext.getUser();
        Long userId = user.getId();
        String originPhoto = user.getPhoto();
        String mapPath = userPhotoService.tryUploadPhoto(multipartFile, userId, originPhoto);
        // 删除记录
        return SystemJsonResponse.SYSTEM_SUCCESS(mapPath);
    }

    @PostMapping("/improve")
    @Operation(summary = "完善用户信息")
    @Tag(name = "用户测试接口/信息")
    public SystemJsonResponse<?> improveUserinfo(@Valid @RequestBody UserinfoDTO userinfoDTO) {
        // 获取当前用户 ID
        Long userId = InterceptorContext.getUser().getId();
        // 完善信息
        userService.improveUserinfo(userinfoDTO, userId);
        // 删除记录
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @GetMapping("/usertype")
    @Operation(summary = "获取用户信息")
    @Tag(name = "用户测试接口/信息")
    @Intercept(authenticate = false, authorize = false)
    public SystemJsonResponse<List<UserTypeVO>> getUserTypeList() {
        List<UserTypeVO> userTypeVOList = UserConverter.INSTANCE.userTypeListToUserTypeVOList(List.of(UserType.values()));
        return SystemJsonResponse.SYSTEM_SUCCESS(userTypeVOList);
    }

    @GetMapping("/userinfo")
    @Operation(summary = "获取用户信息")
    @Tag(name = "用户测试接口/信息")
    @Intercept(authenticate = true, authorize = false) // 只需要登录就能访问
    public SystemJsonResponse<UserVO> getUserInfo() {
        // 获取当前登录用户
        User user = InterceptorContext.getUser();
        // 提取信息
        UserVO userVO = UserConverter.INSTANCE.userToUserVO(user);
        // 返回
        return SystemJsonResponse.SYSTEM_SUCCESS(userVO);
    }
}
