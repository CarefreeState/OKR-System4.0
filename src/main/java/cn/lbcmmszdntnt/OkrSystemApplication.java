package cn.lbcmmszdntnt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"cn.lbcmmszdntnt.domain.**.mapper"})
public class OkrSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(OkrSystemApplication.class, args);
	}

}
