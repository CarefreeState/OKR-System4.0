package cn.bitterfree.api.domain.center.controller;


import cn.bitterfree.api.common.SystemJsonResponse;
import cn.bitterfree.api.common.config.TestDocConfig;
import cn.bitterfree.api.common.util.convert.EncryptUtil;
import cn.bitterfree.api.common.util.media.MediaUtil;
import cn.bitterfree.api.domain.center.model.vo.TestFileVO;
import cn.bitterfree.api.domain.login.model.vo.LoginVO;
import cn.bitterfree.api.domain.media.service.FileMediaService;
import cn.bitterfree.api.domain.qrcode.service.QRCodeService;
import cn.bitterfree.api.interceptor.annotation.Intercept;
import cn.bitterfree.api.interceptor.jwt.TokenVO;
import cn.bitterfree.api.jwt.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Comparator;
import java.util.List;
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
@Intercept
@Tag(name = "中心")
@Validated
@Slf4j
public class CenterController {

    private final TestDocConfig testDocConfig;

    private final QRCodeService qrCodeService;

    private final FileMediaService fileMediaService;

    @Operation(summary = "访问资源")
    @GetMapping({"/{code}", "/", ""})
    @Intercept(authenticate = false, authorize = false)
    public void fileMedia(@PathVariable(value = "code", required = false) @Parameter(description = "资源码") String code,
                          HttpServletResponse response)  {
        fileMediaService.preview(StringUtils.hasText(code) ? code : qrCodeService.getCommonQRCode(), response);
    }

    @Operation(summary = "访问测试文档图片资源")
    @GetMapping({"/testdoc/{path}", "/testdoc", "/testdoc/"})
    @Intercept(authenticate = false, authorize = false)
    public SystemJsonResponse<List<TestFileVO>> testDocFile(@PathVariable(value = "path", required = false) @Parameter(description = "目录") String path) {
        path = Optional.ofNullable(path).map(String::trim).orElse("");
        String filePath = testDocConfig.getImages() + EncryptUtil.decodeBase64(path);
        log.info("查询 {} 目录下的文件夹/文件", filePath);
        List<TestFileVO> list = MediaUtil.getFiles(filePath).sorted(Comparator.comparing(File::getName)).map(file -> {
            return TestFileVO.builder()
                    .isDir(!file.isFile())
                    .name(file.getName())
                    .build();
        }).toList();
        return SystemJsonResponse.SYSTEM_SUCCESS(list);
    }

    @GetMapping("/jwt/{userId}")
    @Operation(summary = "测试阶段获取用户的 token")
    @Intercept(authenticate = false, authorize = false)
    public SystemJsonResponse<LoginVO> getJWTByOpenid(@PathVariable("userId") @NotNull(message = "userId 不能为空") @Parameter(description = "userId") Long userId) {
        // 构造 token
        TokenVO tokenVO = TokenVO.builder().userId(userId).build();
        String token = JwtUtil.createJwt("登录认证（测试阶段伪造）", tokenVO);
        LoginVO loginVO = LoginVO.builder().token(token).build();
        return SystemJsonResponse.SYSTEM_SUCCESS(loginVO);
    }

}
