package cn.bitterfree.domain.user.controller;

import cn.bitterfree.common.SystemJsonResponse;
import cn.bitterfree.domain.user.enums.UserType;
import cn.bitterfree.domain.user.model.converter.UserConverter;
import cn.bitterfree.domain.user.model.dto.UserinfoDTO;
import cn.bitterfree.domain.user.model.entity.User;
import cn.bitterfree.domain.user.model.vo.UserTypeVO;
import cn.bitterfree.domain.user.model.vo.UserVO;
import cn.bitterfree.domain.user.service.UserPhotoService;
import cn.bitterfree.domain.user.service.UserService;
import cn.bitterfree.interceptor.annotation.Intercept;
import cn.bitterfree.interceptor.context.InterceptorContext;
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
@Tag(name = "用户/信息")
@Intercept(permit = {UserType.NORMAL_USER, UserType.MANAGER})
@Validated
public class UserInfoController {

    private final UserService userService;

    private final UserPhotoService userPhotoService;

    @PostMapping(value = "/photo/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传用户头像")
    public SystemJsonResponse<String> uploadPhoto(@Parameter(description = "用户头像（只能上传图片）") @NotNull(message = "用户头像不能为空") @RequestPart("photo") MultipartFile multipartFile) {
        User user = InterceptorContext.getUser();
        Long userId = user.getId();
        String originPhoto = user.getPhoto();
        String mapPath = userPhotoService.tryUploadPhoto(multipartFile, userId, originPhoto);
        return SystemJsonResponse.SYSTEM_SUCCESS(mapPath);
    }

    @PostMapping("/improve")
    @Operation(summary = "完善用户信息")
    public SystemJsonResponse<?> improveUserinfo(@Valid @RequestBody UserinfoDTO userinfoDTO) {
        // 获取当前用户 ID
        Long userId = InterceptorContext.getUser().getId();
        // 完善信息
        userService.improveUserinfo(userinfoDTO, userId);
        // 删除记录
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @GetMapping("/usertype")
    @Operation(summary = "获取用户类型列表")
    @Intercept(authenticate = false, authorize = false)
    public SystemJsonResponse<List<UserTypeVO>> getUserTypeList() {
        List<UserTypeVO> userTypeVOList = UserConverter.INSTANCE.userTypeListToUserTypeVOList(List.of(UserType.values()));
        return SystemJsonResponse.SYSTEM_SUCCESS(userTypeVOList);
    }

    @GetMapping("/userinfo")
    @Operation(summary = "获取用户信息")
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
