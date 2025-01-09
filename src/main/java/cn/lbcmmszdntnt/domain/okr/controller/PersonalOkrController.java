package cn.lbcmmszdntnt.domain.okr.controller;

import cn.lbcmmszdntnt.common.SystemJsonResponse;
import cn.lbcmmszdntnt.domain.okr.model.vo.PersonalOkrVO;
import cn.lbcmmszdntnt.domain.okr.service.PersonalOkrService;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.domain.user.util.UserRecordUtil;
import cn.lbcmmszdntnt.interceptor.annotation.Intercept;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-25
 * Time: 21:35
 */
@RestController
@Tag(name = "个人 OKR 测试接口")
@RequestMapping("/personal")
@RequiredArgsConstructor
@Intercept
public class PersonalOkrController {

    private final PersonalOkrService personalOkrService;

    @GetMapping("/list")
    @Operation(summary = "获取个人 OKR 列表")
    public SystemJsonResponse<List<PersonalOkrVO>> getPersonalOkrs() {
        // 获取当前登录的用户
        User user = UserRecordUtil.getUserRecord();
        // 调用方法
        List<PersonalOkrVO> personalOkrVOS = personalOkrService.getPersonalOkrList(user);
        return SystemJsonResponse.SYSTEM_SUCCESS(personalOkrVOS);
    }


}
