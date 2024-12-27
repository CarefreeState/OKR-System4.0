package cn.lbcmmszdntnt.security.config;

import cn.lbcmmszdntnt.aop.config.PreInterceptConfig;
import cn.lbcmmszdntnt.security.filter.JwtAuthenticationTokenFilter;
import cn.lbcmmszdntnt.security.handler.AuthFailHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 马拉圈
 * Date: 2024-01-11
 * Time: 20:29
 */
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity //开启权限控制，不开启这个，注解的权限控制不能生效
public class SecurityConfig {

    public final static String USER_SECURITY_RECORD = "userSecurityRecord";

    public final static String EXCEPTION_MESSAGE = "exceptionMessage";

    private final JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    private final AuthFailHandler authFailHandler;

    private final IgnoreUrlsConfig ignoreUrlsConfig;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //关闭 csrf
        http.csrf(AbstractHttpConfigurer::disable);
        //不通过 Session 获取 SecurityContext
        http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // 路径管理
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(ignoreUrlsConfig.getUrls().toArray(new String[0])).permitAll()
                .requestMatchers(PreInterceptConfig.SWAGGERS).permitAll()
                .anyRequest().authenticated()
        ).rememberMe(Customizer.withDefaults());
        // 添加过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        // 配置异常处理器（默认的话貌似是抛对应的异常，一大串的东西、或者控制台无表示，用对应的响应状态码表示异常）
        http.exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                .authenticationEntryPoint(authFailHandler)
        );
        // 处理跨域
//        http.cors(cors -> cors.configure(http));
        return http.build();
    }

}
