package cn.bitterfree.api.domain.user.controller;

import cn.bitterfree.api.common.SystemJsonResponse;
import cn.bitterfree.api.domain.media.service.FileMediaService;
import cn.bitterfree.api.domain.user.enums.UserType;
import cn.bitterfree.api.domain.user.service.DefaultPhotoService;
import cn.bitterfree.api.interceptor.annotation.Intercept;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-17
 * Time: 20:40
 */
@RestController
@RequestMapping("/user/defaultphoto")
@RequiredArgsConstructor
@Tag(name = "用户/默认头像管理")
@Intercept(permit = {UserType.MANAGER})
@Validated
public class DefaultPhotoController {

    private final DefaultPhotoService defaultPhotoService;

    private final FileMediaService fileMediaService;

    @Operation(summary = "获取默认头像列表")
    @GetMapping("/list")
    public SystemJsonResponse<List<String>> getDefaultPhotoList() {
        List<String> defaultPhotoList = defaultPhotoService.getDefaultPhotoList();
        return SystemJsonResponse.SYSTEM_SUCCESS(defaultPhotoList);
    }

    @Operation(summary = "添加默认头像")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SystemJsonResponse<String> addDefaultPhoto(@Parameter(description = "默认头像（只能上传图片）") @NotNull(message = "默认头像不能为空") @RequestPart("photo") MultipartFile multipartFile) {
        String code = fileMediaService.uploadImage(multipartFile);
        defaultPhotoService.add(code);
        return SystemJsonResponse.SYSTEM_SUCCESS(code);
    }

    @Operation(summary = "删除默认头像")
    @GetMapping("/remove/{code}")
    public SystemJsonResponse<?> removeDefaultPhoto(@PathVariable("code") @NotBlank(message = "资源码不能为空") @Parameter(description = "资源码") String code) {
        defaultPhotoService.remove(code);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

}
