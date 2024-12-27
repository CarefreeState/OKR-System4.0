package cn.lbcmmszdntnt.domain.okr.controller;


import cn.lbcmmszdntnt.common.SystemJsonResponse;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.qrcode.service.OkrQRCodeService;
import cn.lbcmmszdntnt.domain.user.model.vo.LoginTokenVO;
import cn.lbcmmszdntnt.domain.user.model.vo.LoginVO;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

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

    private final static String JWT_SUBJECT = "登录认证（测试阶段伪造）";

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

    @GetMapping("/jwt/{userId}")
    @Operation(summary = "测试阶段获取用户的 token")
    public SystemJsonResponse<LoginVO> getJWTByOpenid(@PathVariable("userId") @Parameter(description = "userId") Long userId) {
        if(Boolean.FALSE.equals(swaggerCanBeVisited)) {
            // 无法访问 swagger，代表这个接口无法访问
            throw new GlobalServiceException(GlobalServiceStatusCode.SYSTEM_API_VISIT_FAIL);
        }
        // 构造 token
        LoginTokenVO loginTokenVO = LoginTokenVO.builder().userId(userId).build();
        String token = JwtUtil.createJwt(JWT_SUBJECT, loginTokenVO);
        LoginVO loginVO = LoginVO.builder().token(token).build();
        return SystemJsonResponse.SYSTEM_SUCCESS(loginVO);
    }

}
