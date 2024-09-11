package cn.lbcmmszdntnt.domain.core.controller.inner;

import cn.hutool.core.bean.BeanUtil;
import cn.lbcmmszdntnt.common.SystemJsonResponse;
import cn.lbcmmszdntnt.common.constants.SuppressWarningsValue;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.core.model.dto.inner.OkrKeyResultDTO;
import cn.lbcmmszdntnt.domain.core.model.dto.inner.OkrKeyResultUpdateDTO;
import cn.lbcmmszdntnt.domain.core.model.po.inner.KeyResult;
import cn.lbcmmszdntnt.domain.core.model.po.inner.dto.KeyResultDTO;
import cn.lbcmmszdntnt.domain.core.model.po.inner.dto.KeyResultUpdateDTO;
import cn.lbcmmszdntnt.domain.core.service.OkrCoreService;
import cn.lbcmmszdntnt.domain.core.service.inner.KeyResultService;
import cn.lbcmmszdntnt.domain.core.service.quadrant.FirstQuadrantService;
import cn.lbcmmszdntnt.domain.medal.handler.chain.MedalHandlerChain;
import cn.lbcmmszdntnt.domain.medal.model.entry.VictoryWithinGrasp;
import cn.lbcmmszdntnt.domain.okr.factory.OkrOperateServiceFactory;
import cn.lbcmmszdntnt.domain.okr.service.OkrOperateService;
import cn.lbcmmszdntnt.domain.record.handler.chain.RecordEventHandlerChain;
import cn.lbcmmszdntnt.domain.record.model.entry.KeyResultUpdate;
import cn.lbcmmszdntnt.domain.user.model.po.User;
import cn.lbcmmszdntnt.domain.user.util.UserRecordUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.util.thread.pool.IOThreadPool;
import cn.lbcmmszdntnt.util.validation.ValidatorUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "关键结果")
@SuppressWarnings(value = SuppressWarningsValue.SPRING_JAVA_INJECTION_POINT_AUTOWIRING_INSPECTION)
public class KeyResultController {

    private final OkrCoreService okrCoreService;

    private final KeyResultService keyResultService;

    private final OkrOperateServiceFactory okrOperateServiceFactory;

    private final FirstQuadrantService firstQuadrantService;

    private final MedalHandlerChain medalHandlerChain;

    private final RecordEventHandlerChain recordEventHandlerChain;

    @PostMapping("/add")
    @Operation(summary = "添加关键结果")
    public SystemJsonResponse<Long> addKeyResult(@RequestBody OkrKeyResultDTO okrKeyResultDTO) {
        // 校验
        ValidatorUtils.validate(okrKeyResultDTO);
        User user = UserRecordUtil.getUserRecord();
        KeyResultDTO keyResultDTO = okrKeyResultDTO.getKeyResultDTO();
        keyResultDTO.validate();
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrKeyResultDTO.getScene());
        KeyResult keyResult = BeanUtil.copyProperties(keyResultDTO, KeyResult.class);
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
        IOThreadPool.submit(() -> {
            okrCoreService.checkOverThrows(coreId);
            VictoryWithinGrasp victoryWithinGrasp = VictoryWithinGrasp.builder()
                    .userId(userId)
                    .probability(probability)
                    .oldProbability(0)
                    .build();
            medalHandlerChain.handle(victoryWithinGrasp);
            KeyResultUpdate keyResultUpdate = KeyResultUpdate.builder().coreId(coreId).build();
            recordEventHandlerChain.handle(keyResultUpdate);
        });
        return SystemJsonResponse.SYSTEM_SUCCESS(id);
    }

    @PostMapping("/update")
    @Operation(summary = "更新完成概率")
    public SystemJsonResponse updateKeyResult(@RequestBody OkrKeyResultUpdateDTO okrKeyResultUpdateDTO) {
        // 校验
        okrKeyResultUpdateDTO.validate();
        User user = UserRecordUtil.getUserRecord();
        KeyResultUpdateDTO keyResultUpdateDTO = okrKeyResultUpdateDTO.getKeyResultUpdateDTO();
        keyResultUpdateDTO.validate();
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrKeyResultUpdateDTO.getScene());
        KeyResult keyResult = BeanUtil.copyProperties(keyResultUpdateDTO, KeyResult.class);
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
            IOThreadPool.submit(() -> {
                okrCoreService.checkOverThrows(coreId);
                VictoryWithinGrasp victoryWithinGrasp = VictoryWithinGrasp.builder()
                        .userId(userId)
                        .probability(probability)
                        .oldProbability(oldProbability)
                        .build();
                medalHandlerChain.handle(victoryWithinGrasp);
                KeyResultUpdate keyResultUpdate = KeyResultUpdate.builder().coreId(coreId).build();
                recordEventHandlerChain.handle(keyResultUpdate);
            });
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

}
