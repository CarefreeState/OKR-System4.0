package cn.lbcmmszdntnt.domain.okr.init;


import cn.lbcmmszdntnt.config.WebMvcConfiguration;
import cn.lbcmmszdntnt.util.media.MediaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OpenAPISwaggerFileInitializer implements ApplicationListener<ApplicationStartedEvent> {

    private final String suffix = "json";

    @Value("${visit.swagger}")
    private Boolean swaggerCanBeVisited;

    @Value("${server.port}")
    private String port;

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.warn("--> --> --> 应用启动成功 --> 开始加载 swagger 代码 --> --> -->");
        if(Boolean.FALSE.equals(swaggerCanBeVisited)) {
            return;
        }
        String savePath = WebMvcConfiguration.ROOT + WebMvcConfiguration.MAP_ROOT + WebMvcConfiguration.SWAGGER_PATH;
        String filePath = String.format("%s%s.%s", savePath, applicationName, suffix);
        String url = "http://localhost:" + port + "/v3/api-docs";
        MediaUtil.saveFile(savePath, filePath, url);
        log.warn("<-- <-- <-- <-- <-- swagger 代码加载成功 <-- <-- <-- <-- <--");
    }
}
