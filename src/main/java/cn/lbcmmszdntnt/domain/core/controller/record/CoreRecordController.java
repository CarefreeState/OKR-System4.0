package cn.lbcmmszdntnt.domain.core.controller.record;

import cn.lbcmmszdntnt.common.SystemJsonResponse;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.core.model.converter.DayRecordConverter;
import cn.lbcmmszdntnt.domain.core.model.dto.OkrCoreDTO;
import cn.lbcmmszdntnt.domain.core.model.entity.record.DayRecord;
import cn.lbcmmszdntnt.domain.core.model.vo.record.DayRecordVO;
import cn.lbcmmszdntnt.domain.core.service.record.DayRecordService;
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

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-21
 * Time: 12:58
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/corerecord")
@Tag(name = "OKR 记录")
@Intercept
public class CoreRecordController {

    private final OkrOperateServiceFactory okrOperateServiceFactory;

    private final DayRecordService dayRecordService;

    @PostMapping("/search/dayrecord")
    @Operation(summary = "查看一个 OKR 的日记录")
    public SystemJsonResponse<List<DayRecordVO>> searchOkrCoreDayRecord(@Valid @RequestBody OkrCoreDTO okrCoreDTO) {
        User user = InterceptorContext.getUser();
        Long coreId = okrCoreDTO.getCoreId();
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrCoreDTO.getScene());
        if(Boolean.TRUE.equals(okrOperateService.canVisit(user, coreId))) {
            List<DayRecord> dayRecords = dayRecordService.getDayRecords(coreId);
            List<DayRecordVO> dayRecordVOS = DayRecordConverter.INSTANCE.recordListToDayRecordVOList(dayRecords);
            return SystemJsonResponse.SYSTEM_SUCCESS(dayRecordVOS);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
    }
}