package cn.lbcmmszdntnt.domain.core.controller;

import cn.lbcmmszdntnt.common.SystemJsonResponse;
import cn.lbcmmszdntnt.common.constants.SuppressWarningsValue;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.core.model.dto.OkrCoreDTO;
import cn.lbcmmszdntnt.domain.core.model.dto.OkrCoreSummaryDTO;
import cn.lbcmmszdntnt.domain.core.model.dto.OkrOperateDTO;
import cn.lbcmmszdntnt.domain.core.model.vo.OkrCoreVO;
import cn.lbcmmszdntnt.domain.core.service.OkrCoreService;
import cn.lbcmmszdntnt.domain.medal.handler.chain.MedalHandlerChain;
import cn.lbcmmszdntnt.domain.medal.model.entry.OkrFinish;
import cn.lbcmmszdntnt.domain.okr.factory.OkrOperateServiceFactory;
import cn.lbcmmszdntnt.domain.okr.service.OkrOperateService;
import cn.lbcmmszdntnt.domain.user.model.po.User;
import cn.lbcmmszdntnt.domain.user.util.UserRecordUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.util.thread.pool.IOThreadPool;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

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
@Tag(name = "OKR 内核")
@SuppressWarnings(value = SuppressWarningsValue.SPRING_JAVA_INJECTION_POINT_AUTOWIRING_INSPECTION)
public class OkrCoreController {

    private final OkrCoreService okrCoreService;

    private final MedalHandlerChain medalHandlerChain;

    private final OkrOperateServiceFactory okrOperateServiceFactory;

    @PostMapping("/create")
    @Operation(summary = "创建一个 OKR")
    public SystemJsonResponse<Map<String, Object>> createOkr(@RequestBody OkrOperateDTO okrOperateDTO) {
        // 检测
        okrOperateDTO.validate();
        User user = UserRecordUtil.getUserRecord();
//        OkrOperateService okrOperateService = okrServiceSelector.load(okrOperateDTO.getScene());
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrOperateDTO.getScene());
        Map<String, Object> ret = okrOperateService.createOkrCore(user, okrOperateDTO);
        return SystemJsonResponse.SYSTEM_SUCCESS(ret);
    }

    @PostMapping("/search")
    @Operation(summary = "查看一个 OKR")
    public SystemJsonResponse<OkrCoreVO> searchOkrCore(@RequestBody OkrCoreDTO okrCoreDTO) {
        okrCoreDTO.validate();
        User user = UserRecordUtil.getUserRecord();
//        OkrOperateService okrOperateService = okrServiceSelector.load(okrCoreDTO.getScene());
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrCoreDTO.getScene());
        OkrCoreVO okrCoreVO = okrOperateService.selectAllOfCore(user, okrCoreDTO.getCoreId());
        return SystemJsonResponse.SYSTEM_SUCCESS(okrCoreVO);
    }

    @PostMapping("/celebrate/{day}")
    @Operation(summary = "确定庆祝日")
    public SystemJsonResponse confirmCelebrateDay(@RequestBody OkrCoreDTO okrCoreDTO,
                                                  @PathVariable("day") @Parameter(description = "庆祝日（星期）") Integer celebrateDay) {
        if(celebrateDay.compareTo(1) < 0 || celebrateDay.compareTo(7) > 0) {
            throw new GlobalServiceException(GlobalServiceStatusCode.INVALID_CELEBRATE_DAY);
        }
        okrCoreDTO.validate();
        User user = UserRecordUtil.getUserRecord();
        Long coreId = okrCoreDTO.getCoreId();
//        OkrOperateService okrOperateService = okrServiceSelector.load(okrCoreDTO.getScene());
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
    public SystemJsonResponse summaryOKR(@RequestBody OkrCoreSummaryDTO okrCoreSummaryDTO) {
        // 检测
        okrCoreSummaryDTO.validate();
        User user = UserRecordUtil.getUserRecord();
        Long coreId = okrCoreSummaryDTO.getCoreId();
//        OkrOperateService okrOperateService = okrServiceSelector.load(okrCoreSummaryDTO.getScene());
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrCoreSummaryDTO.getScene());
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            String summary = okrCoreSummaryDTO.getSummary();
            Integer degree = okrCoreSummaryDTO.getDegree();
            Date endTime = okrCoreService.summaryOKR(coreId, summary, degree);
            log.info("成功为 OKR {} 总结 {} 完成度 {}%", coreId, summary, degree);
            // 开启一个异步线程
            IOThreadPool.submit(() -> {
                okrCoreService.checkOverThrows(coreId);
                OkrFinish okrFinish = OkrFinish.builder()
                        .userId(userId)
                        .degree(degree)
                        .isAdvance(endTime.compareTo(new Date()) < 0)
                        .build();
                medalHandlerChain.handle(okrFinish);
            });
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @PostMapping("/complete")
    @Operation(summary = "结束 OKR")
    public SystemJsonResponse complete(@RequestBody OkrCoreDTO okrCoreDTO) {
        // 检测
        okrCoreDTO.validate();
        Long coreId = okrCoreDTO.getCoreId();
        User user = UserRecordUtil.getUserRecord();
//        OkrOperateService okrOperateService = okrServiceSelector.load(okrCoreDTO.getScene());
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
