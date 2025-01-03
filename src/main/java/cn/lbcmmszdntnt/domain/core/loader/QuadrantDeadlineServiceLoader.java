package cn.lbcmmszdntnt.domain.core.loader;


import cn.lbcmmszdntnt.domain.core.service.QuadrantDeadlineService;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-12
 * Time: 19:16
 */
@Component
public class QuadrantDeadlineServiceLoader {

    private final ServiceLoader<QuadrantDeadlineService> quadrantDeadlineServices = ServiceLoader.load(QuadrantDeadlineService.class);

    public QuadrantDeadlineService load() {
        // 选取服务
        Iterator<QuadrantDeadlineService> iterator = quadrantDeadlineServices.iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

}
