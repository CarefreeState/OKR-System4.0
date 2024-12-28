package cn.lbcmmszdntnt.util.web;


import cn.hutool.http.HttpRequest;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.util.media.MediaUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class HttpUtil {

    private final static String JSON_CONTENT_TYPE = "application/json; charset=utf-8";

    public static String buildUrl(String baseUrl, Map<String, List<String>> queryParams, Object... uriVariableValues) {
        queryParams = Optional.ofNullable(queryParams).orElseGet(Map::of);
        return UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .queryParams(new LinkedMultiValueMap<>(queryParams))
                .buildAndExpand(uriVariableValues)
                .encode()
                .toUriString();
    }

    public static <P> String buildUrl(String baseUrl, Map<String, List<String>> queryParams, Map<String, P> pathParams) {
        queryParams = Optional.ofNullable(queryParams).orElseGet(Map::of);
        pathParams = Optional.ofNullable(pathParams).orElseGet(Map::of);
        return UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .queryParams(new LinkedMultiValueMap<>(queryParams))
                .buildAndExpand(pathParams)
                .encode()
                .toUriString();
    }

    public static String hiddenQueryString(String url) {
        return StringUtils.hasText(url) && url.contains("?") ? url.substring(0, url.indexOf("?")) : url;
    }

    public static String doGet(String httpUrl) {
        return doGet(httpUrl, null);
    }

    public static String doGet(String httpUrl, Map<String, List<String>> map) {
        return HttpRequest.get(buildUrl(httpUrl, map))
                .execute()
                .body();
    }

    public static String doPostJsonString(String httpUrl, String json) {
        return HttpRequest.post(httpUrl)
                .body(json, JSON_CONTENT_TYPE)
                .execute()
                .body();
    }

    public static byte[] doPostJsonBytes(String httpUrl, String json) {
        return HttpRequest.post(httpUrl)
                .body(json, JSON_CONTENT_TYPE)
                .execute()
                .bodyBytes();
    }

    public static String doPostJsonBase64(String url, String json) {
        return Base64.encodeBase64String(doPostJsonBytes(url, json));
    }

    public static InputStream getFileInputStream(String fileUrl) throws IOException {
        return HttpRequest.get(fileUrl)
                .execute()
                .bodyStream();
    }

    @Nullable
    public static ServletRequestAttributes getAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    @Nullable
    public static HttpServletRequest getRequest() {
        return Optional.ofNullable(getAttributes()).map(ServletRequestAttributes::getRequest).orElse(null);
    }

    @Nullable
    public static HttpServletResponse getResponse() {
        return Optional.ofNullable(getAttributes()).map(ServletRequestAttributes::getResponse).orElse(null);
    }

    public static String encodeString(String str) {
        if(!StringUtils.hasText(str)) {
            return "";
        }
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }

    public static void returnBytes(byte[] bytes, HttpServletResponse response) {
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            // 写入数据
            if(Objects.nonNull(bytes)) {
                // 设置响应内容类型（用同一个 inputStream 会互相影响）
                response.setContentType(MediaUtil.getContentType(bytes));
                // 指定字符集
                response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
                outputStream.write(bytes);
                outputStream.flush();
            }
        } catch (IOException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

    public static void returnBytes(String downloadName, byte[] bytes, HttpServletResponse response) {
        // 在设置内容类型之前设置下载的文件名称
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; fileName=%s", encodeString(downloadName)));
        returnBytes(bytes, response);
    }

}

