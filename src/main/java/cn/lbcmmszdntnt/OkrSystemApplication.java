package cn.lbcmmszdntnt;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"cn.lbcmmszdntnt.domain.**.mapper"})
@OpenAPIDefinition(info = @Info(title = "OKR-System", description = "OKR 目标与管理系统", version = "4.0"))
public class OkrSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(OkrSystemApplication.class, args);
	}

}
