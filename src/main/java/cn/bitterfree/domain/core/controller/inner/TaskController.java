package cn.bitterfree.domain.core.controller.inner;

import cn.bitterfree.common.SystemJsonResponse;
import cn.bitterfree.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.common.exception.GlobalServiceException;
import cn.bitterfree.domain.core.enums.TaskType;
import cn.bitterfree.domain.core.factory.OkrOperateServiceFactory;
import cn.bitterfree.domain.core.factory.TaskServiceFactory;
import cn.bitterfree.domain.core.model.dto.inner.*;
import cn.bitterfree.domain.core.model.message.operate.TaskUpdate;
import cn.bitterfree.domain.core.service.OkrOperateService;
import cn.bitterfree.domain.core.service.inner.TaskService;
import cn.bitterfree.domain.core.util.OkrCoreUpdateMessageUtil;
import cn.bitterfree.domain.user.model.entity.User;
import cn.bitterfree.interceptor.annotation.Intercept;
import cn.bitterfree.interceptor.context.InterceptorContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
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
@Tag(name = "OKR 内核/内件/任务管理")
@Intercept
@Validated
public class TaskController {

    private final OkrOperateServiceFactory okrOperateServiceFactory;

    private final TaskServiceFactory taskServiceFactory;

    @PostMapping("/{option}/add")
    @Operation(summary = "增加一条任务")
    public SystemJsonResponse<?> addTask(
            @PathVariable("option") @Parameter(example = "0", schema = @Schema(
                    type = "integer",
                    format = "int32",
                    description = "任务类型 0 第三象限任务、1 第二象限优先级1、2 第二象限优先级2",
                    allowableValues = {"0", "1", "2"}
            )) @NotNull(message = "option 不能为空") Integer option,
            @Valid @RequestBody OkrTaskDTO okrTaskDTO
    ) {
        // 检查
        User user = InterceptorContext.getUser();
        TaskDTO taskDTO = okrTaskDTO.getTaskDTO();
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrTaskDTO.getScene());
        TaskService taskService = taskServiceFactory.getService(TaskType.get(option));
        // 检测身份
        Long quadrantId = taskDTO.getQuadrantId();
        Long coreId = taskService.getTaskCoreId(quadrantId);
        Long userId = okrOperateService.getCoreUser(coreId);
        Long id = null;
        if (user.getId().equals(userId)) {
            String content = taskDTO.getContent();
            id = taskService.addTask(quadrantId, content);
        } else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS(id);
    }

    @PostMapping("/{option}/remove")
    @Operation(summary = ("删除一个任务"))
    public SystemJsonResponse<?> removeTask(
            @PathVariable("option") @Parameter(example = "0", schema = @Schema(
                    type = "integer",
                    format = "int32",
                    description = "任务类型 0 第三象限任务、1 第二象限优先级1、2 第二象限优先级2",
                    allowableValues = {"0", "1", "2"}
            )) @NotNull(message = "option 不能为空") Integer option,
            @Valid @RequestBody OkrTaskRemoveDTO okrTaskRemoveDTO
    ) {
        // 检查
        User user = InterceptorContext.getUser();
        Long taskId = okrTaskRemoveDTO.getId();
        // 选择服务
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrTaskRemoveDTO.getScene());
        TaskService taskService = taskServiceFactory.getService(TaskType.get(option));
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
    public SystemJsonResponse<?> updateTask(
            @PathVariable("option") @Parameter(example = "0", schema = @Schema(
                    type = "integer",
                    format = "int32",
                    description = "任务类型 0 第三象限任务、1 第二象限优先级1、2 第二象限优先级2",
                    allowableValues = {"0", "1", "2"}
            )) @NotNull(message = "option 不能为空") Integer option,
            @Valid @RequestBody OkrTaskUpdateDTO okrTaskUpdateDTO
    ) {
        // 检查
        TaskUpdateDTO taskUpdateDTO = okrTaskUpdateDTO.getTaskUpdateDTO();
        User user = InterceptorContext.getUser();
        // 选择服务
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrTaskUpdateDTO.getScene());
        TaskType taskType = TaskType.get(option);
        TaskService taskService = taskServiceFactory.getService(taskType);
        Long taskId = taskUpdateDTO.getId();
        // 检测身份
        Long quadrantId = taskService.getTaskQuadrantId(taskId);
        Long coreId = taskService.getTaskCoreId(quadrantId);
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            String content = taskUpdateDTO.getContent();
            Boolean isCompleted = taskUpdateDTO.getIsCompleted();
            Boolean oldCompleted = taskService.updateTask(taskId, content, isCompleted);
            TaskUpdate taskUpdate = TaskUpdate.builder()
                    .taskType(taskType)
                    .coreId(coreId)
                    .userId(userId)
                    .isCompleted(isCompleted)
                    .oldCompleted(oldCompleted)
                    .build();
            OkrCoreUpdateMessageUtil.sendTaskUpdate(taskUpdate);
        } else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }


}
