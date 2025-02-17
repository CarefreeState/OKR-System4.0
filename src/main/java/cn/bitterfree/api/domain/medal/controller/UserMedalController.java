package cn.bitterfree.api.domain.medal.controller;

import cn.bitterfree.api.common.SystemJsonResponse;
import cn.bitterfree.api.domain.medal.model.vo.UserMedalVO;
import cn.bitterfree.api.domain.medal.service.UserMedalService;
import cn.bitterfree.api.interceptor.annotation.Intercept;
import cn.bitterfree.api.interceptor.context.InterceptorContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-08
 * Time: 0:32
 */
@RestController
@Slf4j
@Tag(name = "用户/勋章")
@RequestMapping("/medal")
@RequiredArgsConstructor
@Intercept
@Validated
public class UserMedalController {

    private final UserMedalService userMedalService;

    @GetMapping("/list/all")
    @Operation(summary = "获得用户的所有勋章")
    public SystemJsonResponse<List<UserMedalVO>> getAll() {
        Long userId = InterceptorContext.getUser().getId();
        List<UserMedalVO> result = userMedalService.getUserMedalListAll(userId);
        return SystemJsonResponse.SYSTEM_SUCCESS(result);
    }

    @GetMapping("/list/unread")
    @Operation(summary = "获得用户的所有未读勋章")
    public SystemJsonResponse<List<UserMedalVO>> getUnread() {
        Long userId = InterceptorContext.getUser().getId();
        List<UserMedalVO> result = userMedalService.getUserMedalListUnread(userId);
        log.info("查询用户 {} 的所有未读勋章 : {} 个", userId, result.size());
        return SystemJsonResponse.SYSTEM_SUCCESS(result);
    }

    @PostMapping("/read/{medalId}")
    @Operation(summary = "用户知晓获得了新勋章")
    public SystemJsonResponse<?> readUserMedal(@PathVariable("medalId") @Parameter(description = "勋章 ID") @NotNull(message = "勋章 ID 不能为空") Long medalId) {
        Long userId = InterceptorContext.getUser().getId();
        log.info("用户 {} 查看勋章 {}", userId, medalId);
        userMedalService.readUserMedal(userId, medalId);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

}
