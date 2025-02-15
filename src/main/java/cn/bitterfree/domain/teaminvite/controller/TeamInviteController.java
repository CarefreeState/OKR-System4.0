package cn.bitterfree.domain.teaminvite.controller;

import cn.bitterfree.common.SystemJsonResponse;
import cn.bitterfree.domain.okr.service.TeamOkrService;
import cn.bitterfree.domain.qrcode.enums.QRCodeType;
import cn.bitterfree.domain.teaminvite.model.dto.TeamInviteDTO;
import cn.bitterfree.domain.teaminvite.service.TeamInviteIdentifyService;
import cn.bitterfree.domain.user.model.entity.User;
import cn.bitterfree.interceptor.annotation.Intercept;
import cn.bitterfree.interceptor.context.InterceptorContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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
    public SystemJsonResponse<String> getQRCode(@PathVariable("teamId") @Parameter(description = "团队 OKR ID") @NotNull(message = "团队 OKR ID 不能为空") Long teamId,
                                                @Valid @RequestBody(required = false) TeamInviteDTO teamInviteDTO) {
        // 检测
        User user = InterceptorContext.getUser();
        Long managerId = user.getId();
        // 检测管理者身份
        teamOkrService.checkManager(teamId, managerId);
        // 进行操作
        QRCodeType qrCodeType = Optional.ofNullable(teamInviteDTO).map(TeamInviteDTO::getQrCodeType).orElse(QRCodeType.WX);
        String qrCode = teamInviteIdentifyService.getInviteQRCode(teamId, qrCodeType);
        return SystemJsonResponse.SYSTEM_SUCCESS(qrCode);
    }

}
