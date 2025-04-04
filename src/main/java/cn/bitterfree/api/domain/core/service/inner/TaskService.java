package cn.bitterfree.api.domain.core.service.inner;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 1:24
 */

public interface TaskService {

    Long addTask(Long quadrantId, String content);

    void removeTask(Long id);

    Boolean updateTask(Long id, String content, Boolean isCompleted);

    Long getTaskQuadrantId(Long id);

    Long getTaskCoreId(Long quadrantId);

}
