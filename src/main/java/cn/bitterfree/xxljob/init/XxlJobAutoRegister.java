package cn.bitterfree.xxljob.init;

import cn.bitterfree.xxljob.annotation.XxlRegister;
import cn.bitterfree.xxljob.model.entity.XxlJobGroup;
import cn.bitterfree.xxljob.model.entity.XxlJobInfo;
import cn.bitterfree.xxljob.service.JobGroupService;
import cn.bitterfree.xxljob.service.JobInfoService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.MethodIntrospector;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * 这个类搭配 XxlRegister 使用，是初始化型的 注解
 * 也就是之后这个注解不会产生啥效果
 * 但是在初始化的时候可以被扫描并执行相关的业务
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class XxlJobAutoRegister implements ApplicationListener<ApplicationStartedEvent>{

    private final ApplicationContext applicationContext;

    private final JobGroupService jobGroupService;

    private final JobInfoService jobInfoService;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        //注册执行器
        jobGroupService.saveOrUpdateJobGroup();
        //注册任务
        XxlJobGroup xxlJobGroup = jobGroupService.getJobGroup(0);
        Arrays.stream(applicationContext.getBeanNamesForType(Object.class, Boolean.FALSE, Boolean.TRUE))
                .map(applicationContext::getBean)
                .map(Object::getClass)
                .map(clazz -> MethodIntrospector.selectMethods(
                        clazz,
                        (MethodIntrospector.MetadataLookup<XxlJob>) method -> method.isAnnotationPresent(XxlJob.class) ? method.getAnnotation(XxlJob.class) : null
                ))
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .filter(entry -> entry.getKey().isAnnotationPresent(XxlRegister.class))
                .map(entry -> createXxlJobInfo(xxlJobGroup, entry.getValue(), entry.getKey().getAnnotation(XxlRegister.class)))
                .forEach(jobInfoService::saveOrUpdateJobInfo);
    }

    private XxlJobInfo createXxlJobInfo(XxlJobGroup xxlJobGroup, XxlJob xxlJob, XxlRegister xxlRegister) {
        return XxlJobInfo.builder()
                .jobGroup(xxlJobGroup.getId())
                .jobDesc(xxlRegister.jobDesc())
                .author(xxlRegister.author())
                .alarmEmail(xxlRegister.email())
                .scheduleType("CRON")
                .scheduleConf(xxlRegister.cron())
                .glueType("BEAN")
                .executorHandler(xxlJob.value())
                .executorRouteStrategy(xxlRegister.executorRouteStrategy())
                .misfireStrategy("DO_NOTHING")
                .executorBlockStrategy("SERIAL_EXECUTION")
                .executorTimeout(0)
                .executorFailRetryCount(0)
                .glueRemark("GLUE代码初始化")
                .triggerStatus(xxlRegister.triggerStatus())
                .build();
    }

}