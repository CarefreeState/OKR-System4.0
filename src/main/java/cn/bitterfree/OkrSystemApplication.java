package cn.bitterfree;

import cn.bitterfree.common.constants.DateTimeConstants;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
@MapperScan({"cn.bitterfree.domain.**.mapper"})
public class OkrSystemApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone(DateTimeConstants.TIME_ZONE));
		SpringApplication.run(OkrSystemApplication.class, args);
	}

}
