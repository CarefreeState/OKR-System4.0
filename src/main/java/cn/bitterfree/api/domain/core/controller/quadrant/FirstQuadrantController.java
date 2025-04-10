package cn.bitterfree.api.domain.core.controller.quadrant;

import cn.bitterfree.api.common.SystemJsonResponse;
import cn.bitterfree.api.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.domain.core.factory.OkrOperateServiceFactory;
import cn.bitterfree.api.domain.core.model.converter.FirstQuadrantConverter;
import cn.bitterfree.api.domain.core.model.dto.quadrant.FirstQuadrantDTO;
import cn.bitterfree.api.domain.core.model.dto.quadrant.OkrFirstQuadrantDTO;
import cn.bitterfree.api.domain.core.model.entity.quadrant.FirstQuadrant;
import cn.bitterfree.api.domain.core.model.message.deadline.FirstQuadrantEvent;
import cn.bitterfree.api.domain.core.model.message.operate.OkrInitialize;
import cn.bitterfree.api.domain.core.service.OkrOperateService;
import cn.bitterfree.api.domain.core.service.quadrant.FirstQuadrantService;
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
 * Date: 2024-01-21
 * Time: 22:29
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/firstquadrant")
@Tag(name = "OKR 内核/象限/第一象限")
@Intercept
@Validated
public class FirstQuadrantController {

    private final FirstQuadrantService firstQuadrantService;

    private final OkrOperateServiceFactory okrOperateServiceFactory;

    @PostMapping("/init")
    @Operation(summary = "初始化第一项象限")
    public SystemJsonResponse<?> initFirstQuadrant(@Valid @RequestBody OkrFirstQuadrantDTO okrFirstQuadrantDTO) {
        // 校验
        User user = InterceptorContext.getUser();
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
            OkrCoreUpdateMessageUtil.sendOkrInitialize(OkrInitialize.builder().userId(userId).coreId(coreId).build());
            // 发起一个定时任务
            FirstQuadrantEvent event = FirstQuadrantEvent.builder()
                    .coreId(coreId).deadline(firstQuadrantDTO.getDeadline()).build();
            QuadrantDeadlineMessageUtil.scheduledComplete(event);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        // 成功
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

}
