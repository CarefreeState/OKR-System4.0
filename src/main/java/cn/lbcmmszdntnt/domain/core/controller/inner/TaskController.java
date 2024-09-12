package cn.lbcmmszdntnt.domain.core.controller.inner;

import cn.lbcmmszdntnt.common.SystemJsonResponse;
import cn.lbcmmszdntnt.common.constants.SuppressWarningsValue;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.core.factory.TaskServiceFactory;
import cn.lbcmmszdntnt.domain.core.model.dto.inner.OkrTaskDTO;
import cn.lbcmmszdntnt.domain.core.model.dto.inner.OkrTaskRemoveDTO;
import cn.lbcmmszdntnt.domain.core.model.dto.inner.OkrTaskUpdateDTO;
import cn.lbcmmszdntnt.domain.core.model.po.inner.dto.TaskDTO;
import cn.lbcmmszdntnt.domain.core.model.po.inner.dto.TaskUpdateDTO;
import cn.lbcmmszdntnt.domain.core.service.OkrCoreService;
import cn.lbcmmszdntnt.domain.core.service.TaskService;
import cn.lbcmmszdntnt.domain.medal.factory.TeamAchievementServiceFactory;
import cn.lbcmmszdntnt.domain.medal.service.TermAchievementService;
import cn.lbcmmszdntnt.domain.okr.factory.OkrOperateServiceFactory;
import cn.lbcmmszdntnt.domain.okr.service.OkrOperateService;
import cn.lbcmmszdntnt.domain.record.factory.DayaRecordCompleteServiceFactory;
import cn.lbcmmszdntnt.domain.record.handler.chain.RecordEventHandlerChain;
import cn.lbcmmszdntnt.domain.record.service.DayRecordCompleteService;
import cn.lbcmmszdntnt.domain.user.model.po.User;
import cn.lbcmmszdntnt.domain.user.util.UserRecordUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.util.thread.pool.IOThreadPool;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 1:53
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/task")
@Tag(name = "任务管理")
@SuppressWarnings(value = SuppressWarningsValue.SPRING_JAVA_INJECTION_POINT_AUTOWIRING_INSPECTION)
public class TaskController {

    private final OkrCoreService okrCoreService;

    private final OkrOperateServiceFactory okrOperateServiceFactory;

    private final TaskServiceFactory taskServiceFactory;

    private final TeamAchievementServiceFactory teamAchievementServiceFactory;

    private final DayaRecordCompleteServiceFactory dayaRecordCompleteServiceFactory;

    private final RecordEventHandlerChain recordEventHandlerChain;

    @PostMapping("/{option}/add")
    @Operation(summary = "增加一条任务")
    public SystemJsonResponse addTask(@PathVariable("option") @Parameter(description = "任务选项（0:action, 1:P1, 2:P2）") Integer option,
                                      @Valid @RequestBody OkrTaskDTO okrTaskDTO) {
        // 检查
        User user = UserRecordUtil.getUserRecord();
        TaskDTO taskDTO = okrTaskDTO.getTaskDTO();
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrTaskDTO.getScene());
        TaskService taskService = taskServiceFactory.getService(option);
        // 检测身份
        Long quadrantId = taskDTO.getQuadrantId();
        Long coreId = taskService.getTaskCoreId(quadrantId);
        Long userId = okrOperateService.getCoreUser(coreId);
        Long id = null;
        if(user.getId().equals(userId)) {
            String content = taskDTO.getContent();
            id = taskService.addTask(quadrantId, content);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS(id);
    }

    @PostMapping("/{option}/remove")
    @Operation(summary = ("删除一个任务"))
    public SystemJsonResponse removeTask(@PathVariable("option") @Parameter(description = "任务选项（0:action, 1:P1, 2:P2）") Integer option,
                                         @Valid @RequestBody OkrTaskRemoveDTO okrTaskRemoveDTO) {
        // 检查
        User user = UserRecordUtil.getUserRecord();
        Long taskId = okrTaskRemoveDTO.getId();
        // 选择服务
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrTaskRemoveDTO.getScene());
        TaskService taskService = taskServiceFactory.getService(option);
        // 检测身份
        Long quadrantId = taskService.getTaskQuadrantId(taskId);
        Long coreId = taskService.getTaskCoreId(quadrantId);
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            taskService.removeTask(taskId);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @PostMapping("/{option}/update")
    @Operation(summary = "更新一条任务")
    public SystemJsonResponse updateTask(@PathVariable("option") @Parameter(description = "任务选项（0:Action, 1:P1, 2:P2）") Integer option,
                                         @Valid @RequestBody OkrTaskUpdateDTO okrTaskUpdateDTO) {
        // 检查
        TaskUpdateDTO taskUpdateDTO = okrTaskUpdateDTO.getTaskUpdateDTO();
        User user = UserRecordUtil.getUserRecord();
        // 选择服务
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrTaskUpdateDTO.getScene());
        TaskService taskService = taskServiceFactory.getService(option);
        Long taskId = taskUpdateDTO.getId();
        // 检测身份
        Long quadrantId = taskService.getTaskQuadrantId(taskId);
        Long coreId = taskService.getTaskCoreId(quadrantId);
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            String content = taskUpdateDTO.getContent();
            Boolean isCompleted = taskUpdateDTO.getIsCompleted();
            Boolean oldCompleted = taskService.updateTask(taskId, content, isCompleted);
            // 开启两个异步线程
            IOThreadPool.submit(() -> {
                okrCoreService.checkOverThrows(coreId);
                TermAchievementService termAchievementService = teamAchievementServiceFactory.getService(option);
                termAchievementService.issueTermAchievement(userId, isCompleted, oldCompleted);
                DayRecordCompleteService dayRecordCompleteService = dayaRecordCompleteServiceFactory.getService(option);
                recordEventHandlerChain.handle(dayRecordCompleteService.getEvent(coreId, isCompleted, oldCompleted));
            });
        } else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }


}
