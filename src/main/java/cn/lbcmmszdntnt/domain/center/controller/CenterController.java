package cn.lbcmmszdntnt.domain.center.controller;


import cn.lbcmmszdntnt.common.SystemJsonResponse;
import cn.lbcmmszdntnt.domain.login.model.vo.LoginVO;
import cn.lbcmmszdntnt.domain.media.service.FileMediaService;
import cn.lbcmmszdntnt.domain.qrcode.service.QRCodeService;
import cn.lbcmmszdntnt.interceptor.annotation.Intercept;
import cn.lbcmmszdntnt.interceptor.jwt.TokenVO;
import cn.lbcmmszdntnt.jwt.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-02-22
 * Time: 4:14
 */
@RestController
@RequiredArgsConstructor
@Intercept
@Tag(name = "中心")
@Validated
public class CenterController {

    private final QRCodeService qrCodeService;

    private final FileMediaService fileMediaService;

    @Operation(summary = "访问资源")
    @GetMapping({"/{code}", "/", ""})
    @Intercept(authenticate = false, authorize = false)
    public void fileMedia(@PathVariable(value = "code", required = false) @Parameter(description = "资源码") String code,
                          HttpServletResponse response)  {
        fileMediaService.preview(StringUtils.hasText(code) ? code : qrCodeService.getCommonQRCode(), response);
    }

    @GetMapping("/jwt/{userId}")
    @Operation(summary = "测试阶段获取用户的 token")
    @Intercept(authenticate = false, authorize = false)
    public SystemJsonResponse<LoginVO> getJWTByOpenid(@PathVariable("userId") @Parameter(description = "userId") Long userId) {
        // 构造 token
        TokenVO tokenVO = TokenVO.builder().userId(userId).build();
        String token = JwtUtil.createJwt("登录认证（测试阶段伪造）", tokenVO);
        LoginVO loginVO = LoginVO.builder().token(token).build();
        return SystemJsonResponse.SYSTEM_SUCCESS(loginVO);
    }

}
