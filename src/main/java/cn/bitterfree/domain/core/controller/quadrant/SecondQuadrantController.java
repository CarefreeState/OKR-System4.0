package cn.bitterfree.domain.core.controller.quadrant;

import cn.bitterfree.common.SystemJsonResponse;
import cn.bitterfree.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.common.exception.GlobalServiceException;
import cn.bitterfree.domain.core.config.QuadrantCycleConfig;
import cn.bitterfree.domain.core.factory.OkrOperateServiceFactory;
import cn.bitterfree.domain.core.model.dto.quadrant.InitQuadrantDTO;
import cn.bitterfree.domain.core.model.dto.quadrant.OkrInitQuadrantDTO;
import cn.bitterfree.domain.core.model.message.operate.OkrInitialize;
import cn.bitterfree.domain.core.service.OkrOperateService;
import cn.bitterfree.domain.core.service.quadrant.SecondQuadrantService;
import cn.bitterfree.domain.core.util.OkrCoreUpdateMessageUtil;
import cn.bitterfree.domain.user.model.entity.User;
import cn.bitterfree.interceptor.annotation.Intercept;
import cn.bitterfree.interceptor.context.InterceptorContext;
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
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            secondQuadrantService.initSecondQuadrant(initQuadrantDTO);
            log.info("第二象限初始化成功：{}", initQuadrantDTO);
            OkrCoreUpdateMessageUtil.sendOkrInitialize(OkrInitialize.builder().userId(userId).coreId(coreId).build());
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        // 成功
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }


}
