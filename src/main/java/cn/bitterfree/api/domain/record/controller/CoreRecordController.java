package cn.bitterfree.api.domain.record.controller;

import cn.bitterfree.api.common.SystemJsonResponse;
import cn.bitterfree.api.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.domain.core.factory.OkrOperateServiceFactory;
import cn.bitterfree.api.domain.core.model.dto.OkrCoreDTO;
import cn.bitterfree.api.domain.core.service.OkrOperateService;
import cn.bitterfree.api.domain.record.model.converter.DayRecordConverter;
import cn.bitterfree.api.domain.record.model.entity.DayRecord;
import cn.bitterfree.api.domain.record.model.vo.DayRecordVO;
import cn.bitterfree.api.domain.record.service.DayRecordService;
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
@Tag(name = "OKR 内核/记录")
@Intercept
@Validated
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
