package cn.bitterfree.api.domain.core.controller;

import cn.bitterfree.api.common.SystemJsonResponse;
import cn.bitterfree.api.common.annotation.IntRange;
import cn.bitterfree.api.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.domain.core.factory.OkrOperateServiceFactory;
import cn.bitterfree.api.domain.core.model.dto.OkrCoreDTO;
import cn.bitterfree.api.domain.core.model.dto.OkrCoreSummaryDTO;
import cn.bitterfree.api.domain.core.model.dto.OkrOperateDTO;
import cn.bitterfree.api.domain.core.model.message.operate.OkrFinish;
import cn.bitterfree.api.domain.core.model.vo.OKRCreateVO;
import cn.bitterfree.api.domain.core.model.vo.OkrCoreVO;
import cn.bitterfree.api.domain.core.service.OkrCoreService;
import cn.bitterfree.api.domain.core.service.OkrOperateService;
import cn.bitterfree.api.domain.core.util.OkrCoreUpdateMessageUtil;
import cn.bitterfree.api.domain.user.model.entity.User;
import cn.bitterfree.api.interceptor.annotation.Intercept;
import cn.bitterfree.api.interceptor.context.InterceptorContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-19
 * Time: 23:57
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/core")
@Validated
@Tag(name = "OKR 内核")
@Intercept
public class OkrCoreController {

    private final OkrCoreService okrCoreService;

    private final OkrOperateServiceFactory okrOperateServiceFactory;

    @PostMapping("/create")
    @Operation(summary = "创建一个 OKR")
    public SystemJsonResponse<OKRCreateVO> createOkr(@Valid @RequestBody OkrOperateDTO okrOperateDTO) {
        // 检测
        User user = InterceptorContext.getUser();
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrOperateDTO.getScene());
        OKRCreateVO okrCreateVO = okrOperateService.createOkrCore(user, okrOperateDTO);
        return SystemJsonResponse.SYSTEM_SUCCESS(okrCreateVO);
    }

    @PostMapping("/search")
    @Operation(summary = "查看一个 OKR")
    public SystemJsonResponse<OkrCoreVO> searchOkrCore(@Valid @RequestBody OkrCoreDTO okrCoreDTO) {
        User user = InterceptorContext.getUser();
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrCoreDTO.getScene());
        OkrCoreVO okrCoreVO = okrOperateService.selectAllOfCore(user, okrCoreDTO.getCoreId());
        return SystemJsonResponse.SYSTEM_SUCCESS(okrCoreVO);
    }

    @PostMapping("/celebrate/{day}")
    @Operation(summary = "确定庆祝日")
    public SystemJsonResponse<?> confirmCelebrateDay(@Valid @RequestBody OkrCoreDTO okrCoreDTO,
                                                     @NotNull(message = "庆祝日（星期）不能为空") @IntRange (min = 1, max = 7) @PathVariable("day") @Parameter(example = "1", description = "庆祝日（星期）") Integer celebrateDay) {
        User user = InterceptorContext.getUser();
        Long coreId = okrCoreDTO.getCoreId();
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrCoreDTO.getScene());
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)){
            okrCoreService.confirmCelebrateDate(coreId, celebrateDay);
            log.info("成功为 OKR {} 确定庆祝日 星期{}", coreId, celebrateDay);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @PostMapping("/summary")
    @Operation(summary = "总结 OKR")
    public SystemJsonResponse<?> summaryOKR(@Valid @RequestBody OkrCoreSummaryDTO okrCoreSummaryDTO) {
        // 检测
        User user = InterceptorContext.getUser();
        Long coreId = okrCoreSummaryDTO.getCoreId();
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrCoreSummaryDTO.getScene());
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            String summary = okrCoreSummaryDTO.getSummary();
            Integer degree = okrCoreSummaryDTO.getDegree();
            Date endTime = okrCoreService.summaryOKR(coreId, summary, degree);
            log.info("成功为 OKR {} 总结 {} 完成度 {}%", coreId, summary, degree);
            OkrFinish okrFinish = OkrFinish.builder()
                    .userId(userId)
                    .degree(degree)
                    .isAdvance(endTime.compareTo(new Date()) < 0)
                    .build();
            OkrCoreUpdateMessageUtil.sendOkrFinish(okrFinish);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @PostMapping("/complete")
    @Operation(summary = "结束 OKR")
    public SystemJsonResponse<?> complete(@Valid @RequestBody OkrCoreDTO okrCoreDTO) {
        // 检测
        Long coreId = okrCoreDTO.getCoreId();
        User user = InterceptorContext.getUser();
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrCoreDTO.getScene());
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            okrCoreService.complete(coreId);
            log.info("成功结束 OKR {}", coreId);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

}
