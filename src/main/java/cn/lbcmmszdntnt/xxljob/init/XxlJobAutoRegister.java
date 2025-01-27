package cn.lbcmmszdntnt.xxljob.init;

import cn.lbcmmszdntnt.xxljob.annotation.XxlRegister;
import cn.lbcmmszdntnt.xxljob.model.entity.XxlJobGroup;
import cn.lbcmmszdntnt.xxljob.model.entity.XxlJobInfo;
import cn.lbcmmszdntnt.xxljob.service.JobGroupService;
import cn.lbcmmszdntnt.xxljob.service.JobInfoService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

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
        jobGroupService.addJobGroup();
        //注册任务
        XxlJobGroup xxlJobGroup = jobGroupService.getJobGroupOne(0);
        String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, Boolean.FALSE, Boolean.TRUE);
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);
            Map<Method, XxlJob> annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                    (MethodIntrospector.MetadataLookup<XxlJob>) method -> AnnotatedElementUtils.findMergedAnnotation(method, XxlJob.class));
            annotatedMethods.forEach((executeMethod, xxljob) -> {
                //自动注册
                if (executeMethod.isAnnotationPresent(XxlRegister.class)) {
                    XxlRegister xxlRegister = executeMethod.getAnnotation(XxlRegister.class);
                    List<XxlJobInfo> jobInfo = jobInfoService.getJobInfo(xxlJobGroup.getId(), xxljob.value());
                    XxlJobInfo xxlJobInfo = createXxlJobInfo(xxlJobGroup, xxljob, xxlRegister);
                    if (!jobInfo.isEmpty()) {
                        //因为是模糊查询，需要再判断一次
                        boolean isPresent = jobInfo.stream()
                                .anyMatch(info -> info.getExecutorHandler().equals(xxljob.value()));
                        // 无需新增
                        if (isPresent) return;
                    }
                    jobInfoService.addJob(xxlJobInfo);
                }
            });
        }
    }

    private XxlJobInfo createXxlJobInfo(XxlJobGroup xxlJobGroup, XxlJob xxlJob, XxlRegister xxlRegister) {
        return XxlJobInfo.builder()
                .jobGroup(xxlJobGroup.getId())
                .jobDesc(xxlRegister.jobDesc())
                .author(xxlRegister.author())
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