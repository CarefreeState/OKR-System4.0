package cn.lbcmmszdntnt.domain.okr.controller;


import cn.lbcmmszdntnt.common.SystemJsonResponse;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.okr.model.dto.GrantDTO;
import cn.lbcmmszdntnt.domain.okr.model.dto.TeamUpdateDTO;
import cn.lbcmmszdntnt.domain.okr.model.entity.TeamOkr;
import cn.lbcmmszdntnt.domain.okr.model.vo.TeamOkrStatisticVO;
import cn.lbcmmszdntnt.domain.okr.model.vo.TeamOkrVO;
import cn.lbcmmszdntnt.domain.okr.service.MemberService;
import cn.lbcmmszdntnt.domain.okr.service.TeamOkrService;
import cn.lbcmmszdntnt.domain.okr.util.TeamOkrUtil;
import cn.lbcmmszdntnt.domain.qrcode.enums.QRCodeType;
import cn.lbcmmszdntnt.domain.qrcode.service.OkrQRCodeService;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.domain.user.util.UserRecordUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.interceptor.annotation.Intercept;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-25
 * Time: 22:19
 */
@RestController
@Tag(name = "团队 OKR 测试接口")
@RequestMapping("/team")
@RequiredArgsConstructor
@Slf4j
@Intercept
public class TeamOkrController {

    private final TeamOkrService teamOkrService;

    private final MemberService memberService;

    private final OkrQRCodeService okrQRCodeService;

    @GetMapping("/list")
    @Operation(summary = "获取管理的团队 OKR 列表")
    public SystemJsonResponse<List<TeamOkrVO>> getTeamOkrs() {
        // 获取当前登录的用户
        User user = UserRecordUtil.getUserRecord();
        // 调用方法
        List<TeamOkrVO> teamOkrVOS = teamOkrService.getTeamOkrList(user);
        return SystemJsonResponse.SYSTEM_SUCCESS(teamOkrVOS);
    }

    @PostMapping("/rename")
    @Operation(summary = "修改团队的名字")
    public SystemJsonResponse updateName(@Valid @RequestBody TeamUpdateDTO teamUpdateDTO) {
        // 获取当前登录用户
        User user = UserRecordUtil.getUserRecord();
        // 判断是不是管理员
        Long managerId = user.getId();
        Long teamId = teamUpdateDTO.getId();
        String teamName = teamUpdateDTO.getTeamName();
        // 检测管理者身份
        teamOkrService.checkManager(teamId, managerId);
        // 更新
        TeamOkr updateTeam = new TeamOkr();
        updateTeam.setId(teamId);
        updateTeam.setTeamName(teamName);
        teamOkrService.lambdaUpdate().eq(TeamOkr::getId, teamId).update(updateTeam);
        // 删除缓存
        teamOkrService.deleteTeamNameCache(teamId);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @PostMapping("/tree/{id}")
    @Operation(summary = "获取一个团队所在的树")
    public SystemJsonResponse<List<TeamOkrStatisticVO>> getCompleteTree(@PathVariable("id") @Parameter(description = "团队 OKR ID") Long id) {
        // 获取当前团队的祖先 ID
        Long rootId = TeamOkrUtil.getTeamRootId(id);
        User user = UserRecordUtil.getUserRecord();
        Long userId = user.getId();
        // 判断是否是其中的成员
        memberService.checkExistsInTeam(rootId, userId);
        // 获取根团队的所有孩子节点
        List<Long> ids = TeamOkrUtil.getChildIds(rootId);
        if(ids.isEmpty()) {
            throw new GlobalServiceException(GlobalServiceStatusCode.TEAM_NOT_EXISTS);
        }
        // 计算完成度
        List<TeamOkrStatisticVO> statisticVOS = teamOkrService.countCompletionRate(ids);
        log.info("查询团队 {} 在 根团队 {} 中， 树的总节点数为 {}", id, rootId, statisticVOS.size());
        return SystemJsonResponse.SYSTEM_SUCCESS(statisticVOS);
    }

    @PostMapping("/tree/child/{id}")
    @Operation(summary = "获取一个团队的子树")
    public SystemJsonResponse<List<TeamOkrStatisticVO>> getChildTree(@PathVariable("id") @Parameter(description = "团队 OKR ID") Long id) {
        // 获取当前团队的祖先 ID
        User user = UserRecordUtil.getUserRecord();
        Long userId = user.getId();
        // 判断是否是其中的成员
        memberService.checkExistsInTeam(id, userId);
        // 获取根团队的所有孩子节点
        List<Long> ids = TeamOkrUtil.getChildIds(id);
        if(ids.isEmpty()) {
            throw new GlobalServiceException(GlobalServiceStatusCode.TEAM_NOT_EXISTS);
        }
        log.info("查询团队 {} 的子树， 树的总节点数为 {}", id, ids.size());
        // 计算完成度
        List<TeamOkrStatisticVO> statisticVOS = teamOkrService.countCompletionRate(ids);
        return SystemJsonResponse.SYSTEM_SUCCESS(statisticVOS);
    }

    @PostMapping("/grant")
    @Operation(summary = "给成员授权，使其可以扩展一个子团队")
    public SystemJsonResponse<Map<String, Object>> grantTeamForMember(@Valid @RequestBody GrantDTO grantDTO) {
        // 获取当前管理员 ID
        User user = UserRecordUtil.getUserRecord();
        Long managerId = user.getId();
        Long userId = grantDTO.getUserId();
        Long teamId = grantDTO.getTeamId();
        String teamName = grantDTO.getTeamName();
        Map<String, Object> ret = teamOkrService.grantTeamForMember(teamId, managerId, userId, teamName);
        return SystemJsonResponse.SYSTEM_SUCCESS(ret);
    }

    @PostMapping("/qrcode/{teamId}")
    @Operation(summary = "获取邀请码")
    public SystemJsonResponse<String> getQRCode(@PathVariable("teamId") @Parameter(description = "团队 OKR ID") Long teamId,
                                                @RequestParam(value = "type", required = false) @Parameter(example = "wx", schema = @Schema(
                                                        type = "string",
                                                        description = "二维码类型 wx 微信小程序二维码、web 网页二维码",
                                                        allowableValues = {"wx", "web"}
                                                )) String type) {
        // 检测
        User user = UserRecordUtil.getUserRecord();
        Long managerId = user.getId();
        // 检测管理者身份
        teamOkrService.checkManager(teamId, managerId);
        // 进行操作
        String path = okrQRCodeService.getInviteQRCodeLock(teamId, TeamOkrUtil.getTeamName(teamId), QRCodeType.get(type));
        return SystemJsonResponse.SYSTEM_SUCCESS(path);
    }

    @GetMapping("/describe/{teamId}")
    @Operation(summary = "了解团队")
    @Intercept(authenticate = false, authorize = false)
    public SystemJsonResponse<String> getTeamName(@PathVariable("teamId") @Parameter(description = "团队 OKR ID") Long teamId) throws IOException {
        String teamName = TeamOkrUtil.getTeamName(teamId);
        return SystemJsonResponse.SYSTEM_SUCCESS(teamName);
    }
}
