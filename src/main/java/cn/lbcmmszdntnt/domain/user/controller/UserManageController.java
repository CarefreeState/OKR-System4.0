package cn.lbcmmszdntnt.domain.user.controller;

import cn.lbcmmszdntnt.common.SystemJsonResponse;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.common.exception.GlobalServiceException;
import cn.lbcmmszdntnt.domain.user.enums.UserType;
import cn.lbcmmszdntnt.domain.user.model.dto.UserQueryDTO;
import cn.lbcmmszdntnt.domain.user.model.dto.UserTypeUpdateDTO;
import cn.lbcmmszdntnt.domain.user.model.vo.UserQueryVO;
import cn.lbcmmszdntnt.domain.user.service.UserPhotoService;
import cn.lbcmmszdntnt.domain.user.service.UserService;
import cn.lbcmmszdntnt.interceptor.annotation.Intercept;
import cn.lbcmmszdntnt.interceptor.context.InterceptorContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-17
 * Time: 16:20
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户/信息管理")
@Intercept(permit = {UserType.MANAGER})
@Validated
public class UserManageController {

    private final UserService userService;

    private final UserPhotoService userPhotoService;

    @Operation(summary = "更新用户类型")
    @PostMapping("/update/type/{userId}")
    public SystemJsonResponse<?> updateUserType(@PathVariable("userId") @NotNull(message = "用户 id 不能为空") @Parameter(description = "用户 id") Long userId,
                                                @Valid @RequestBody UserTypeUpdateDTO userTypeUpdateDTO) {
        Long currentUserId = InterceptorContext.getUser().getId();
        if(currentUserId.equals(userId)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NO_PERMISSION);
        }
        userService.updateUserType(userId, userTypeUpdateDTO.getUserType());
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @Operation(summary = "重置用户头像")
    @PostMapping("/reset/photo/{userId}")
    public SystemJsonResponse<?> resetUserPhoto(@PathVariable("userId") @NotNull(message = "用户 id 不能为空") @Parameter(description = "用户 id") Long userId) {
        userService.getUserById(userId).ifPresent(user -> {
            userPhotoService.updateUserPhoto(userPhotoService::getAnyOnePhoto, userId, user.getPhoto());
        });
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @Operation(summary = "条件分页查询用户")
    @PostMapping("/query")
    public SystemJsonResponse<UserQueryVO> queryUser(@Valid @RequestBody(required = false) UserQueryDTO userQueryDTO) {
        UserQueryVO userQueryVO = userService.queryUser(userQueryDTO);
        return SystemJsonResponse.SYSTEM_SUCCESS(userQueryVO);
    }

}
