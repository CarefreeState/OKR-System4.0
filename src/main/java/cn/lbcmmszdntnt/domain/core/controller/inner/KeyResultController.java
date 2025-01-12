package cn.lbcmmszdntnt.domain.core.controller.inner;

import cn.lbcmmszdntnt.common.SystemJsonResponse;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.core.model.converter.KeyResultConverter;
import cn.lbcmmszdntnt.domain.core.model.dto.inner.KeyResultDTO;
import cn.lbcmmszdntnt.domain.core.model.dto.inner.KeyResultUpdateDTO;
import cn.lbcmmszdntnt.domain.core.model.dto.inner.OkrKeyResultDTO;
import cn.lbcmmszdntnt.domain.core.model.dto.inner.OkrKeyResultUpdateDTO;
import cn.lbcmmszdntnt.domain.core.model.entity.inner.KeyResult;
import cn.lbcmmszdntnt.domain.core.model.event.KeyResultUpdate;
import cn.lbcmmszdntnt.domain.core.service.inner.KeyResultService;
import cn.lbcmmszdntnt.domain.core.service.quadrant.FirstQuadrantService;
import cn.lbcmmszdntnt.domain.core.util.OkrCoreUpdateEventUtil;
import cn.lbcmmszdntnt.domain.okr.factory.OkrOperateServiceFactory;
import cn.lbcmmszdntnt.domain.okr.service.OkrOperateService;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.interceptor.annotation.Intercept;
import cn.lbcmmszdntnt.interceptor.context.InterceptorContext;
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
 * Time: 2:21
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/keyresult")
@Tag(name = "OKR 内核/内件/关键结果")
@Intercept
public class KeyResultController {

    private final KeyResultService keyResultService;

    private final OkrOperateServiceFactory okrOperateServiceFactory;

    private final FirstQuadrantService firstQuadrantService;

    @PostMapping("/add")
    @Operation(summary = "添加关键结果")
    public SystemJsonResponse<Long> addKeyResult(@Valid @RequestBody OkrKeyResultDTO okrKeyResultDTO) {
        // 校验
        User user = InterceptorContext.getUser();
        KeyResultDTO keyResultDTO = okrKeyResultDTO.getKeyResultDTO();
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrKeyResultDTO.getScene());
        KeyResult keyResult = KeyResultConverter.INSTANCE.keyResultDTOToKeyResult(keyResultDTO);
        // 检测身份
        Long firstQuadrantId = keyResultDTO.getFirstQuadrantId();
        Long coreId = firstQuadrantService.getFirstQuadrantCoreId(firstQuadrantId);
        Long userId = okrOperateService.getCoreUser(coreId);
        Long id = null;
        if(user.getId().equals(userId)) {
            // 添加
            id = keyResultService.addResultService(keyResult);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        Integer probability = keyResult.getProbability();
        KeyResultUpdate keyResultUpdate = KeyResultUpdate.builder()
                .userId(userId)
                .coreId(coreId)
                .probability(probability)
                .oldProbability(0)
                .build();
        OkrCoreUpdateEventUtil.sendKeyResultUpdate(keyResultUpdate);
        return SystemJsonResponse.SYSTEM_SUCCESS(id);
    }

    @PostMapping("/update")
    @Operation(summary = "更新完成概率")
    public SystemJsonResponse<?> updateKeyResult(@Valid @RequestBody OkrKeyResultUpdateDTO okrKeyResultUpdateDTO) {
        // 校验
        User user = InterceptorContext.getUser();
        KeyResultUpdateDTO keyResultUpdateDTO = okrKeyResultUpdateDTO.getKeyResultUpdateDTO();
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrKeyResultUpdateDTO.getScene());
        KeyResult keyResult = KeyResultConverter.INSTANCE.keyResultUpdateDTOToKeyResult(keyResultUpdateDTO);
        Long keyResultId = keyResult.getId();
        // 校验身份
        Long firstQuadrantId = keyResultService.getFirstQuadrantId(keyResultId);
        Long coreId = firstQuadrantService.getFirstQuadrantCoreId(firstQuadrantId);
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            // 更新
            KeyResult oldKeyResult = keyResultService.updateProbability(keyResult);
            log.info("提交更新：{}", keyResultUpdateDTO);
            Integer probability = keyResult.getProbability();
            Integer oldProbability = oldKeyResult.getProbability();
            KeyResultUpdate keyResultUpdate = KeyResultUpdate.builder()
                    .userId(userId)
                    .coreId(coreId)
                    .probability(probability)
                    .oldProbability(oldProbability)
                    .build();
            OkrCoreUpdateEventUtil.sendKeyResultUpdate(keyResultUpdate);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

}
