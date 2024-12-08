package cn.lbcmmszdntnt.common.constants;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


@Configuration
@EnableWebMvc
public interface DateTimeConstants {

    /**
     * 定义全局默认时间序列化
     */
    String TIME_ZONE = "Asia/Shanghai";

    String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    String DATE_PATTERN = "yyyy-MM-dd";

    DateFormat DATE_TIME_FORMAT = new SimpleDateFormat(DATE_TIME_PATTERN);

    DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);

}

