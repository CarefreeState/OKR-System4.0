package cn.lbcmmszdntnt.domain.medal.controller;

import cn.lbcmmszdntnt.common.SystemJsonResponse;
import cn.lbcmmszdntnt.domain.medal.model.vo.UserMedalVO;
import cn.lbcmmszdntnt.domain.medal.service.UserMedalService;
import cn.lbcmmszdntnt.domain.user.util.UserRecordUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Tag(name = "用户勋章测试接口")
@RequestMapping("/medal")
@RequiredArgsConstructor
public class UserMedalController {

    private final UserMedalService userMedalService;

    @GetMapping("/list/all")
    @Operation(description = "获得用户的所有勋章")
    public SystemJsonResponse<List<UserMedalVO>> getAll() {
        Long userId = UserRecordUtil.getUserRecord().getId();
        List<UserMedalVO> result = userMedalService.getUserMedalListAll(userId);
        return SystemJsonResponse.SYSTEM_SUCCESS(result);
    }

    @GetMapping("/list/unread")
    @Operation(description = "获得用户的所有未读勋章")
    public SystemJsonResponse<List<UserMedalVO>> getUnread() {
        Long userId = UserRecordUtil.getUserRecord().getId();
        List<UserMedalVO> result = userMedalService.getUserMedalListUnread(userId);
        log.info("查询用户 {} 的所有未读勋章 : {} 个", userId, result.size());
        return SystemJsonResponse.SYSTEM_SUCCESS(result);
    }

    @PostMapping("/read/{medalId}")
    @Operation(description = "用户知晓获得了新勋章")
    public SystemJsonResponse readUserMedal(@PathVariable("medalId") @Parameter(description = "勋章 ID") Long medalId) {
        Long userId = UserRecordUtil.getUserRecord().getId();
        log.info("用户 {} 查看勋章 {}", userId, medalId);
        userMedalService.readUserMedal(userId, medalId);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

}
