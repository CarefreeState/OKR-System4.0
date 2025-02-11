package cn.bitterfree.domain.userbinding.controller;

import cn.bitterfree.common.SystemJsonResponse;
import cn.bitterfree.domain.user.enums.UserType;
import cn.bitterfree.domain.user.model.entity.User;
import cn.bitterfree.domain.user.service.UserService;
import cn.bitterfree.domain.userbinding.enums.BindingType;
import cn.bitterfree.domain.userbinding.factory.BindingServiceFactory;
import cn.bitterfree.domain.userbinding.model.dto.BindingDTO;
import cn.bitterfree.domain.userbinding.model.dto.EmailBindingDTO;
import cn.bitterfree.domain.userbinding.service.BindingService;
import cn.bitterfree.interceptor.annotation.Intercept;
import cn.bitterfree.interceptor.context.InterceptorContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 22:44
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "绑定")
@Validated
public class UserBindingController {

    private final UserService userService;

    private final BindingServiceFactory bindingServiceFactory;

    @PostMapping("/user/binding/email")
    @Operation(summary = "绑定用户邮箱")
    @Intercept(permit = {UserType.NORMAL_USER, UserType.MANAGER})
    public SystemJsonResponse<?> emailBinding(@Valid @RequestBody EmailBindingDTO emailBindingDTO) {
        // 获取当前登录的用户
        User user = InterceptorContext.getUser();
        BindingDTO bindingDTO = new BindingDTO();
        bindingDTO.setEmailBindingDTO(emailBindingDTO);
        // 尝试绑定
        bindingServiceFactory.getService(BindingType.EMAIL).binding(user, bindingDTO);
        // 清除记录
        userService.clearUserAllCache(user.getId());
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @PostMapping("/user/binding")
    @Operation(summary = "用户绑定")
    @Intercept(permit = {UserType.NORMAL_USER, UserType.MANAGER})
    public SystemJsonResponse<?> binding(@Valid @RequestBody BindingDTO bindingDTO) {
        User user = InterceptorContext.getUser();
        BindingService bindingService = bindingServiceFactory.getService(bindingDTO.getType());
        bindingService.binding(user, bindingDTO);
        // 清除缓存
        userService.clearUserAllCache(user.getId());
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

}
