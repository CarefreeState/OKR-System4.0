package cn.bitterfree.api.domain.core.controller.quadrant;

import cn.bitterfree.api.common.SystemJsonResponse;
import cn.bitterfree.api.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.domain.core.config.QuadrantCycleConfig;
import cn.bitterfree.api.domain.core.factory.OkrOperateServiceFactory;
import cn.bitterfree.api.domain.core.model.dto.quadrant.InitQuadrantDTO;
import cn.bitterfree.api.domain.core.model.dto.quadrant.OkrInitQuadrantDTO;
import cn.bitterfree.api.domain.core.model.message.deadline.SecondQuadrantEvent;
import cn.bitterfree.api.domain.core.model.message.operate.OkrInitialize;
import cn.bitterfree.api.domain.core.service.OkrCoreService;
import cn.bitterfree.api.domain.core.service.OkrOperateService;
import cn.bitterfree.api.domain.core.service.quadrant.SecondQuadrantService;
import cn.bitterfree.api.domain.core.util.OkrCoreUpdateMessageUtil;
import cn.bitterfree.api.domain.core.util.QuadrantDeadlineMessageUtil;
import cn.bitterfree.api.domain.user.model.entity.User;
import cn.bitterfree.api.interceptor.annotation.Intercept;
import cn.bitterfree.api.interceptor.context.InterceptorContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 12:57
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/secondquadrant")
@Tag(name = "OKR 内核/象限/第二象限")
@Intercept
@Validated
public class SecondQuadrantController {

    private final QuadrantCycleConfig quadrantCycleConfig;

    private final SecondQuadrantService secondQuadrantService;

    private final OkrCoreService okrCoreService;

    private final OkrOperateServiceFactory okrOperateServiceFactory;

    @PostMapping("/init")
    @Operation(summary = "初始化第二象限")
    public SystemJsonResponse<?> initSecondQuadrant(@Valid @RequestBody OkrInitQuadrantDTO okrInitQuadrantDTO) {
        // 初始化
        InitQuadrantDTO initQuadrantDTO = okrInitQuadrantDTO.getInitQuadrantDTO();
        Integer quadrantCycle = initQuadrantDTO.getQuadrantCycle();
        // 判断周期长度合理性
        if(quadrantCycleConfig.getSecond().compareTo(quadrantCycle) > 0) {
            throw new GlobalServiceException(GlobalServiceStatusCode.SECOND_CYCLE_TOO_SHORT);
        }
        User user = InterceptorContext.getUser();
        Long quadrantId = initQuadrantDTO.getId();
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrInitQuadrantDTO.getScene());
        // 检测身份
        Long coreId = secondQuadrantService.getSecondQuadrantCoreId(quadrantId);
        okrCoreService.checkOverThrows(coreId);
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            secondQuadrantService.initSecondQuadrant(initQuadrantDTO);
            okrCoreService.removeOkrCoreCache(coreId);
            log.info("第二象限初始化成功：{}", initQuadrantDTO);
            OkrCoreUpdateMessageUtil.sendOkrInitialize(OkrInitialize.builder().userId(userId).coreId(coreId).build());
            // 发起一个定时任务
            SecondQuadrantEvent event = SecondQuadrantEvent.builder()
                    .coreId(coreId).id(quadrantId).cycle(quadrantCycle).deadline(initQuadrantDTO.getDeadline())
                    .build();
            QuadrantDeadlineMessageUtil.scheduledUpdateSecondQuadrant(event);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        // 成功
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }


}
