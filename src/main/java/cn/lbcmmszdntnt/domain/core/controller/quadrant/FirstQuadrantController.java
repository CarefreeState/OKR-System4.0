package cn.lbcmmszdntnt.domain.core.controller.quadrant;

import cn.lbcmmszdntnt.common.SystemJsonResponse;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.core.factory.OkrOperateServiceFactory;
import cn.lbcmmszdntnt.domain.core.model.converter.FirstQuadrantConverter;
import cn.lbcmmszdntnt.domain.core.model.dto.quadrant.FirstQuadrantDTO;
import cn.lbcmmszdntnt.domain.core.model.dto.quadrant.OkrFirstQuadrantDTO;
import cn.lbcmmszdntnt.domain.core.model.entity.quadrant.FirstQuadrant;
import cn.lbcmmszdntnt.domain.core.model.message.operate.OkrInitialize;
import cn.lbcmmszdntnt.domain.core.service.OkrOperateService;
import cn.lbcmmszdntnt.domain.core.service.quadrant.FirstQuadrantService;
import cn.lbcmmszdntnt.domain.core.util.OkrCoreUpdateMessageUtil;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.interceptor.annotation.Intercept;
import cn.lbcmmszdntnt.interceptor.context.InterceptorContext;
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
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        // 成功
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

}
