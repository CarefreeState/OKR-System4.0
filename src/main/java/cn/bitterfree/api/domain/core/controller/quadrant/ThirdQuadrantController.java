package cn.bitterfree.api.domain.core.controller.quadrant;

import cn.bitterfree.api.common.SystemJsonResponse;
import cn.bitterfree.api.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.domain.core.factory.OkrOperateServiceFactory;
import cn.bitterfree.api.domain.core.model.dto.quadrant.InitQuadrantDTO;
import cn.bitterfree.api.domain.core.model.dto.quadrant.OkrInitQuadrantDTO;
import cn.bitterfree.api.domain.core.model.message.operate.OkrInitialize;
import cn.bitterfree.api.domain.core.service.OkrCoreService;
import cn.bitterfree.api.domain.core.service.OkrOperateService;
import cn.bitterfree.api.domain.core.service.quadrant.ThirdQuadrantService;
import cn.bitterfree.api.domain.core.util.OkrCoreUpdateMessageUtil;
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
 * Time: 13:20
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/thirdquadrant")
@Tag(name = "OKR 内核/象限/第三象限")
@Intercept
@Validated
public class ThirdQuadrantController {

    private final ThirdQuadrantService thirdQuadrantService;

    private final OkrOperateServiceFactory okrOperateServiceFactory;

    private final OkrCoreService okrCoreService;

    @PostMapping("/init")
    @Operation(summary = "初始化第三象限")
    public SystemJsonResponse<?> initThirdQuadrant(@Valid @RequestBody OkrInitQuadrantDTO okrInitQuadrantDTO) {
        // 初始化
        InitQuadrantDTO initQuadrantDTO = okrInitQuadrantDTO.getInitQuadrantDTO();
        Integer quadrantCycle = initQuadrantDTO.getQuadrantCycle();
        Long quadrantId = initQuadrantDTO.getId();
        Long coreId = thirdQuadrantService.getThirdQuadrantCoreId(quadrantId);
        User user = InterceptorContext.getUser();

        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrInitQuadrantDTO.getScene());
        // 检测身份
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            // 判断时长是否合理
            okrCoreService.checkThirdCycle(coreId, quadrantCycle);
            thirdQuadrantService.initThirdQuadrant(initQuadrantDTO);
            log.info("第三象限初始化成功：{}", initQuadrantDTO);
            OkrCoreUpdateMessageUtil.sendOkrInitialize(OkrInitialize.builder().userId(userId).coreId(coreId).build());
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

}
