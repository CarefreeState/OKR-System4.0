package cn.lbcmmszdntnt.domain.okr.controller;

import cn.lbcmmszdntnt.common.SystemJsonResponse;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.okr.model.entity.TeamPersonalOkr;
import cn.lbcmmszdntnt.domain.okr.model.vo.TeamMemberVO;
import cn.lbcmmszdntnt.domain.okr.model.vo.TeamPersonalOkrVO;
import cn.lbcmmszdntnt.domain.okr.service.MemberService;
import cn.lbcmmszdntnt.domain.okr.service.TeamOkrService;
import cn.lbcmmszdntnt.domain.okr.service.TeamPersonalOkrService;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.domain.user.util.UserRecordUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.interceptor.annotation.Intercept;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-25
 * Time: 22:57
 */
@RestController
@Tag(name = "OKR/团队个人 OKR 测试接口")
@RequestMapping("/teampersonal")
@RequiredArgsConstructor
@Intercept
public class TeamPersonalOkrController {

    private final MemberService memberService;

    private final TeamPersonalOkrService teamPersonalOkrService;

    private final TeamOkrService teamOkrService;

    @GetMapping("/list")
    @Operation(summary = "获取团队个人 OKR 列表")
    public SystemJsonResponse<List<TeamPersonalOkrVO>> getTeamOkrs() {
        // 获取当前登录的用户
        User user = UserRecordUtil.getUserRecord();
        // 调用方法
        List<TeamPersonalOkrVO> teamPersonalOkrVOS = teamPersonalOkrService.getTeamPersonalOkrList(user);
        return SystemJsonResponse.SYSTEM_SUCCESS(teamPersonalOkrVOS);
    }

    @PostMapping("/members/{teamId}")
    @Operation(summary = "获取团队成员列表")
    public SystemJsonResponse<List<TeamMemberVO>> getTeamMember(@PathVariable("teamId") @Parameter(description = "团队 OKR ID") Long teamId) {
        // 获取当前登录用户
        User user = UserRecordUtil.getUserRecord();
        // 判断是不是团队成员
        memberService.checkExistsInTeam(teamId, user.getId());
        // 查询
        List<TeamMemberVO> teamMembers = teamPersonalOkrService.getTeamMembers(teamId);
        return SystemJsonResponse.SYSTEM_SUCCESS(teamMembers);
    }

    @GetMapping("/remove/{id}")
    @Operation(summary = "移除成员")
    public SystemJsonResponse removeMember(@PathVariable("id") @Parameter(description = "团队个人 OKR ID") Long id) {
        // 查询团队个人 Okr
        TeamPersonalOkr teamPersonalOkr = Db.lambdaQuery(TeamPersonalOkr.class)
                .eq(TeamPersonalOkr::getId, id)
                .oneOpt()
                .orElseThrow(() ->
                        new GlobalServiceException(GlobalServiceStatusCode.MEMBER_NOT_EXISTS)
                );
        Long teamId = teamPersonalOkr.getTeamId();
        Long useId = teamPersonalOkr.getUserId();
        // 获取当前登录用户
        User user = UserRecordUtil.getUserRecord();
        // 判断是不是团队成员
        memberService.checkExistsInTeam(teamId, useId);
        teamOkrService.checkManager(teamId, user.getId());
        // 尝试删除
        memberService.removeMember(teamId, id, useId);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

}
