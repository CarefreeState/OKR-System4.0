package cn.lbcmmszdntnt.domain.user.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.lbcmmszdntnt.aop.config.VisitConfig;
import cn.lbcmmszdntnt.common.SystemJsonResponse;
import cn.lbcmmszdntnt.common.constants.SuppressWarningsValue;
import cn.lbcmmszdntnt.domain.email.factory.EmailServiceFactory;
import cn.lbcmmszdntnt.domain.email.util.IdentifyingCodeValidator;
import cn.lbcmmszdntnt.domain.qrcode.model.vo.LoginQRCodeVO;
import cn.lbcmmszdntnt.domain.qrcode.service.OkrQRCodeService;
import cn.lbcmmszdntnt.domain.user.factory.LoginServiceFactory;
import cn.lbcmmszdntnt.domain.user.model.dto.EmailBindingDTO;
import cn.lbcmmszdntnt.domain.user.model.dto.EmailCheckDTO;
import cn.lbcmmszdntnt.domain.user.model.dto.UserinfoDTO;
import cn.lbcmmszdntnt.domain.user.model.dto.WxBindingDTO;
import cn.lbcmmszdntnt.domain.user.model.dto.unify.LoginDTO;
import cn.lbcmmszdntnt.domain.user.model.po.User;
import cn.lbcmmszdntnt.domain.user.model.vo.UserVO;
import cn.lbcmmszdntnt.domain.user.service.LoginService;
import cn.lbcmmszdntnt.domain.user.service.UserService;
import cn.lbcmmszdntnt.domain.user.sse.server.SseUserServer;
import cn.lbcmmszdntnt.domain.user.util.UserRecordUtil;
import cn.lbcmmszdntnt.domain.user.websocket.server.WsUserServer;
import cn.lbcmmszdntnt.sse.util.SseMessageSender;
import cn.lbcmmszdntnt.util.convert.JsonUtil;
import cn.lbcmmszdntnt.websocket.util.MessageSender;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-20
 * Time: 0:07
 */
@RestController
@Schema(name = "用户测试接口")
@RequestMapping("/user")
@RequiredArgsConstructor
@SuppressWarnings(value = SuppressWarningsValue.SPRING_JAVA_INJECTION_POINT_AUTOWIRING_INSPECTION)
public class UserController {

    private final LoginServiceFactory loginServiceFactory;

    private final EmailServiceFactory emailServiceFactory;

    private final UserService userService;

    private final OkrQRCodeService okrQRCodeService;

    public static void main(String[] args) {
        System.out.println(Math.pow(10, -3));
        System.out.println(1e-3);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public SystemJsonResponse<Map<String, Object>> login(HttpServletRequest request,
                                                         @RequestBody LoginDTO loginDTO) {
        String type = request.getHeader(VisitConfig.HEADER);
        // 检查
        loginDTO.validate();
        // 选取服务
        LoginService loginService = loginServiceFactory.getService(type);
        Map<String, Object> result = loginService.login(loginDTO);
        return SystemJsonResponse.SYSTEM_SUCCESS(result);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public SystemJsonResponse logout(HttpServletRequest request) {
        String type = request.getHeader(VisitConfig.HEADER);
        // 选取服务
        LoginService loginService = loginServiceFactory.getService(type);
        loginService.logout(request);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @PostMapping("/check/email")
    @Operation(summary = "验证邮箱用户")
    public SystemJsonResponse emailIdentityCheck(@RequestBody EmailCheckDTO emailCheckDTO) {
        emailCheckDTO.validate();
        // 获得随机验证码
        String code = IdentifyingCodeValidator.getIdentifyingCode();
        String type = emailCheckDTO.getType();
        String email = emailCheckDTO.getEmail();
        emailServiceFactory
                .getService(type)
                .sendIdentifyingCode(email, code);
        // 能到这一步就成功了
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @PostMapping("/check/wx")
    @Operation(summary = "验证微信用户")
    public SystemJsonResponse<String> wxIdentifyCheck() {
        Long userId = UserRecordUtil.getUserRecord().getId();
        String randomCode = IdentifyingCodeValidator.getIdentifyingCode();
        // 生成一个小程序检查码
        String mapPath = okrQRCodeService.getBindingQRCode(userId, randomCode);
        return SystemJsonResponse.SYSTEM_SUCCESS(mapPath);
    }

    @GetMapping("/wx/login")
    @Operation(summary = "获取微信登录码")
    public SystemJsonResponse<LoginQRCodeVO> wxLoginCheck() {
        // 生成一个小程序检查码
        LoginQRCodeVO result = okrQRCodeService.getLoginQRCode();
        return SystemJsonResponse.SYSTEM_SUCCESS(result);
    }

//    @PostMapping("/wx/confirm/{secret}")
//    @Operation(summary = "微信登录确认")
//    public SystemJsonResponse wxLoginConfirm(@PathVariable("secret") @Parameter(description = "secret") String secret) {
//        User user = UserRecordUtil.getUserRecord();
//        String openid = user.getOpenid();
//        String unionid = user.getUnionid();
//        userService.onLoginState(secret, openid, unionid);//如果不是微信用户，token 的数据没意义，对不上
//        return SystemJsonResponse.SYSTEM_SUCCESS();
//    }

    @PostMapping("/wx/confirm/{secret}")
    @Operation(summary = "微信登录确认")
    public SystemJsonResponse wxLoginConfirm(@PathVariable("secret") @Parameter(description = "secret") String secret) {
        User user = UserRecordUtil.getUserRecord();
        String openid = user.getOpenid();
        String unionid = user.getUnionid();
        userService.onLoginState(secret, openid, unionid);//如果不是微信用户，但是有 openid，说明这个用户等同于微信登录
        // 发送已确认的通知
        SystemJsonResponse systemJsonResponse = SystemJsonResponse.SYSTEM_SUCCESS();
        String message = JsonUtil.analyzeData(systemJsonResponse);
        MessageSender.sendMessageToOne(WsUserServer.WEB_SOCKET_USER_SERVER + secret, message);
        SseMessageSender.sendMessage(SseUserServer.SSE_USER_SERVER + secret, message);
        return systemJsonResponse;
    }

    @PostMapping("/wx/login/{secret}")
    @Operation(summary = "微信登录检查")
    public SystemJsonResponse<Map<String, Object>> wxLoginCheck(@PathVariable("secret") @Parameter(description = "secret") String secret) {
        Map<String, Object> result = userService.checkLoginState(secret);
        return SystemJsonResponse.SYSTEM_SUCCESS(result);
    }

    @PostMapping("/binding/email")
    @Operation(summary = "绑定用户邮箱")
    public SystemJsonResponse emailBinding(HttpServletRequest request,
                                           @RequestBody EmailBindingDTO emailBindingDTO) {
        emailBindingDTO.validate();
        String email = emailBindingDTO.getEmail();
        String code = emailBindingDTO.getCode();
        // 获取当前登录的用户
        // todo: 考察是否要限制绑定次数，或者是否可以重新绑定，当前不做限制
        User userRecord = UserRecordUtil.getUserRecord();
        Long userId = userRecord.getId();
        String recordEmail = userRecord.getEmail();
        userService.bindingEmail(userId, email, code, recordEmail);
        UserRecordUtil.deleteUserRecord(request);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @PostMapping("/binding/wx")
    @Operation(summary = "绑定用户微信")
    public SystemJsonResponse wxBinding(HttpServletRequest request,
                                           @RequestBody WxBindingDTO wxBindingDTO) {
        wxBindingDTO.validate();
        Long userId = wxBindingDTO.getUserId();
        String randomCode = wxBindingDTO.getRandomCode();
        String code = wxBindingDTO.getCode();
        userService.bindingWx(userId, randomCode, code);
        UserRecordUtil.deleteUserRecord(request);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @PostMapping("/photo/upload")
    @Operation(summary = "上传用户头像")
    public SystemJsonResponse<String> uploadPhoto(HttpServletRequest request,
            @Parameter(description = "用户头像（只能上传图片，最大 1MB）") @RequestPart("photo") MultipartFile multipartFile) throws IOException {
        byte[] photoData = multipartFile.getBytes();
        User user = UserRecordUtil.getUserRecord();
        Long userId = user.getId();
        String originPhoto = user.getPhoto();
        String mapPath = userService.tryUploadPhoto(photoData, userId, originPhoto);
        // 删除记录
        UserRecordUtil.deleteUserRecord(request);
        return SystemJsonResponse.SYSTEM_SUCCESS(mapPath);
    }

    @PostMapping("/improve")
    @Operation(summary = "完善用户信息")
    public SystemJsonResponse improveUserinfo(HttpServletRequest request,
                                              @RequestBody UserinfoDTO userinfoDTO) {
        // 获取当前用户 ID
        Long userId = UserRecordUtil.getUserRecord().getId();
        // 完善信息
        userService.improveUserinfo(userinfoDTO, userId);
        // 删除记录
        UserRecordUtil.deleteUserRecord(request);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @GetMapping("/userinfo")
    @Operation(summary = "获取用户信息")
    public SystemJsonResponse<UserVO> getUserInfo() {
        // 获取当前登录用户
        User user = UserRecordUtil.getUserRecord();
        // 提取信息
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        // 返回
        return SystemJsonResponse.SYSTEM_SUCCESS(userVO);
    }
}