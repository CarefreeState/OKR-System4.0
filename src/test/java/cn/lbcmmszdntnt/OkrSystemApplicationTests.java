package cn.lbcmmszdntnt;

import com.baomidou.mybatisplus.core.toolkit.AES;
import org.junit.jupiter.api.Test;

//@SpringBootTest
class OkrSystemApplicationTests {

	@Test
	void contextLoads() {
		String randomKey = "{OKR(Lmz171309)}";
		System.out.printf("--mpw.key=%s\n", randomKey);

		// 利用密钥对用户名加密
		String username = AES.encrypt("root", randomKey);
		System.out.printf("mpw:%s\n", username);

		// 利用密钥对密码加密
		String password = AES.encrypt("mmsszsd666", randomKey);
		System.out.printf("mpw:%s\n", password);

		// 利用密钥解密
		System.out.println(AES.decrypt(username, randomKey));
		System.out.println(AES.decrypt(password, randomKey));
	}

}
