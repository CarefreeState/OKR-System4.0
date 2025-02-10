package cn.bitterfree.domain.teaminvite.controller;

import cn.bitterfree.common.SystemJsonResponse;
import cn.bitterfree.domain.okr.service.TeamOkrService;
import cn.bitterfree.domain.qrcode.enums.QRCodeType;
import cn.bitterfree.domain.teaminvite.service.TeamInviteIdentifyService;
import cn.bitterfree.domain.user.model.entity.User;
import cn.bitterfree.interceptor.annotation.Intercept;
import cn.bitterfree.interceptor.context.InterceptorContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 19:38
 */
@RestController
@Tag(name = "OKR/团队 OKR")
@RequiredArgsConstructor
@Slf4j
@Intercept
@Validated
public class TeamInviteController {

    private final TeamOkrService teamOkrService;

    private final TeamInviteIdentifyService teamInviteIdentifyService;

    @PostMapping("/team/qrcode/{teamId}")
    @Operation(summary = "获取邀请码")
    public SystemJsonResponse<String> getQRCode(
            @PathVariable("teamId") @Parameter(description = "团队 OKR ID") Long teamId,
            @RequestParam(value = "type", required = false) @Parameter(example = "wx", schema = @Schema(
                    type = "string",
                    description = "二维码类型 wx 微信小程序二维码、web 网页二维码",
                    allowableValues = {"wx", "web"}
            )) String type
    ) {
        // 检测
        User user = InterceptorContext.getUser();
        Long managerId = user.getId();
        // 检测管理者身份
        teamOkrService.checkManager(teamId, managerId);
        // 进行操作
        String qrCode = teamInviteIdentifyService.getInviteQRCode(teamId, QRCodeType.get(type));
        return SystemJsonResponse.SYSTEM_SUCCESS(qrCode);
    }

}
