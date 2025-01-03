package cn.lbcmmszdntnt.domain.core.controller.quadrant;

import cn.lbcmmszdntnt.aop.config.AfterInterceptConfig;
import cn.lbcmmszdntnt.common.SystemJsonResponse;
import cn.lbcmmszdntnt.common.constants.SuppressWarningsValue;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.core.model.converter.FirstQuadrantConverter;
import cn.lbcmmszdntnt.domain.core.model.dto.quadrant.OkrFirstQuadrantDTO;
import cn.lbcmmszdntnt.domain.core.model.po.quadrant.FirstQuadrant;
import cn.lbcmmszdntnt.domain.core.model.po.quadrant.dto.FirstQuadrantDTO;
import cn.lbcmmszdntnt.domain.core.service.quadrant.FirstQuadrantService;
import cn.lbcmmszdntnt.domain.okr.factory.OkrOperateServiceFactory;
import cn.lbcmmszdntnt.domain.okr.service.OkrOperateService;
import cn.lbcmmszdntnt.domain.user.model.po.User;
import cn.lbcmmszdntnt.domain.user.util.UserRecordUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.common.util.thread.local.ThreadLocalMapUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 22:29
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/firstquadrant")
@Tag(name = "第一象限")
@SuppressWarnings(value = SuppressWarningsValue.SPRING_JAVA_INJECTION_POINT_AUTOWIRING_INSPECTION)
public class FirstQuadrantController {

    private final FirstQuadrantService firstQuadrantService;

    private final OkrOperateServiceFactory okrOperateServiceFactory;

    @PostMapping("/init")
    @Operation(summary = "初始化第一项象限")
    public SystemJsonResponse initFirstQuadrant(@Valid @RequestBody OkrFirstQuadrantDTO okrFirstQuadrantDTO) {
        // 校验
        User user = UserRecordUtil.getUserRecord();
        FirstQuadrantDTO firstQuadrantDTO = okrFirstQuadrantDTO.getFirstQuadrantDTO();
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrFirstQuadrantDTO.getScene());
        FirstQuadrant firstQuadrant = FirstQuadrantConverter.INSTANCE.firstQuadrantDTOToFirstQuadrant(firstQuadrantDTO);
        Long firstQuadrantId = firstQuadrant.getId();
        // 检测身份
        Long coreId = firstQuadrantService.getFirstQuadrantCoreId(firstQuadrantId);
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            firstQuadrantService.initFirstQuadrant(firstQuadrant);
            log.info("第一象限初始化成功：{}", firstQuadrantDTO);
            ThreadLocalMapUtil.set(AfterInterceptConfig.CORE_ID, coreId);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        // 成功
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

}
