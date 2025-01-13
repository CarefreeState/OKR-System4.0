package cn.lbcmmszdntnt.domain.coredeadline.init;


import cn.lbcmmszdntnt.domain.coredeadline.service.OkrCoreDeadlineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeadlineEventInitializer implements ApplicationListener<ApplicationStartedEvent> {

    private final OkrCoreDeadlineService okrCoreDeadlineService;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.warn("--> --> --> 应用启动成功 --> 开始检查 OKR 截止时间 --> --> -->");
        okrCoreDeadlineService.checkDeadline();
        log.warn("<-- <-- <-- <-- <-- 检查完毕成功 <-- <-- <-- <-- <--");
    }
}
