package cn.lbcmmszdntnt.domain.okr.controller;


import cn.lbcmmszdntnt.common.SystemJsonResponse;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.qrcode.service.OkrQRCodeService;
import cn.lbcmmszdntnt.domain.user.util.ExtractUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.security.config.SecurityConfig;
import cn.lbcmmszdntnt.security.handler.AuthFailRedirectHandler;
import cn.lbcmmszdntnt.util.convert.JsonUtil;
import cn.lbcmmszdntnt.util.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-02-22
 * Time: 4:14
 */
@RestController
@RequiredArgsConstructor
public class CenterController {

    private final static String ROOT_HTML = "root.html";

    @Value("${spring.domain}")
    private String domain;

    @Value("${visit.swagger}")
    private Boolean swaggerCanBeVisited;

    private final OkrQRCodeService okrQRCodeService;

    @GetMapping("/")
    public RedirectView rootHtml()  {
        String htmlUrl = domain + "/" + okrQRCodeService.getCommonQRCode();
        return new RedirectView(htmlUrl);
    }

    @RequestMapping(AuthFailRedirectHandler.REDIRECT_URL)
    public SystemJsonResponse unlisted(@RequestParam(value = SecurityConfig.EXCEPTION_MESSAGE, required = false) String exceptionMessage) {
        throw new GlobalServiceException(
                Optional.ofNullable(exceptionMessage).orElseGet(GlobalServiceStatusCode.USER_NOT_LOGIN::getMessage),
                GlobalServiceStatusCode.USER_NOT_LOGIN
        );
    }

    @GetMapping("/jwt/{openid}")
    @Operation(summary = "测试阶段获取微信用户的 token")
    public SystemJsonResponse<String> getJWTByOpenid(@PathVariable("openid") @Parameter(description = "openid") String openid) {
        if(Boolean.FALSE.equals(swaggerCanBeVisited)) {
            // 无法访问 swagger，代表这个接口无法访问
            throw new GlobalServiceException(GlobalServiceStatusCode.SYSTEM_API_VISIT_FAIL);
        }
        Map<String, Object> tokenData = new HashMap<>(){{
            this.put(ExtractUtil.OPENID, openid);
        }};
        String jsonData = JsonUtil.analyzeData(tokenData);
        String token = JwtUtil.createJwt(jsonData);
        return SystemJsonResponse.SYSTEM_SUCCESS(token);
    }

}
