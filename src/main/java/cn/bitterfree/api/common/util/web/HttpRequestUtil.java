package cn.bitterfree.api.common.util.web;


import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.common.util.convert.JsonUtil;
import cn.bitterfree.api.common.util.convert.ObjectUtil;
import cn.bitterfree.api.common.util.media.MediaUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class HttpRequestUtil {

    private final static Map<String, String> JSON_CONTENT_TYPE_HEADER = Map.of(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");
    private final static PathMatcher PATH_MATCHER = new AntPathMatcher();

    // 这里的参数 setCookies 的元素，一定有 key=value
    public static String convertCookie(List<String> setCookies) {
        return ObjectUtil.distinctNonNullStream(setCookies)
                .map(setCookie -> {
                    int index = setCookie.indexOf(";");
                    return index < 0 ? setCookie : setCookie.substring(0, index);
                }) // Set-Cookie 第一个分句就是 key=value
                .collect(Collectors.joining("; "));
    }

    public static boolean matchPath(String pattern, String path) {
        return PATH_MATCHER.match(pattern, path);
    }

    public static boolean anyMatchPath(List<String> patterns, String path) {
        return ObjectUtil.distinctNonNullStream(patterns)
                .anyMatch(pattern -> matchPath(pattern, path));
    }

    public static String getHost(HttpServletRequest request) {
        return String.format("%s://%s", request.getScheme(), request.getHeader(HttpHeaders.HOST));
    }

    public static String getBaseUrl(String domain, String... uris) {
        String uri = Arrays.stream(uris).filter(StringUtils::hasText).collect(Collectors.joining());
        return domain + uri;
    }

    public static String getBaseUrl(HttpServletRequest request, String... uris) {
        return getBaseUrl(getHost(request), uris);
    }

    public static String buildUrl(String baseUrl, Map<String, List<String>> queryParams, Object... uriVariableValues) {
        queryParams = Optional.ofNullable(queryParams).orElseGet(Map::of);
        return UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .encode() // 开启译码模式，这里之后路径参数，/ 也会被转义为 %2F！
                .queryParams(new LinkedMultiValueMap<>(queryParams))
                .buildAndExpand(uriVariableValues)
                .toUriString();
    }

    public static <P> String buildUrl(String baseUrl, Map<String, List<String>> queryParams, Map<String, P> pathParams) {
        queryParams = Optional.ofNullable(queryParams).orElseGet(Map::of);
        pathParams = Optional.ofNullable(pathParams).orElseGet(Map::of);
        return UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .encode() // 开启译码模式，这里之后路径参数，/ 也会被转义为 %2F！
                .queryParams(new LinkedMultiValueMap<>(queryParams))
                .buildAndExpand(pathParams)
                .toUriString();
    }

    public static String hiddenQueryString(String url) {
        return StringUtils.hasText(url) && url.contains("?") ? url.substring(0, url.indexOf("?")) : url;
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

    public static <T, R> R jsonRequest(String url, String method, T requestBody, Class<R> responseClazz, Map<String, String> headers) {
        try(HttpResponse execute = jsonRequest(url, method, requestBody, headers)) {
            String respJson = execute.body();
            // 转换并返回
            return JsonUtil.parse(respJson, responseClazz);
        }
    }

    public static <T> HttpResponse jsonRequest(String url, String method, T requestBody, Map<String, String> headers) {
        // 准备参数
        Method requestMethod = Method.valueOf(method.toUpperCase());
        headers = Optional.ofNullable(headers).orElseGet(Map::of);
        // 发出请求
        HttpRequest httpRequest = HttpUtil.createRequest(requestMethod, url)
                .headerMap(headers, Boolean.TRUE)
                .headerMap(JSON_CONTENT_TYPE_HEADER, Boolean.TRUE);
        if(Objects.nonNull(requestBody)) {
            String reqJson = JsonUtil.toJson(requestBody);
            httpRequest = httpRequest.body(reqJson);
        }
        return httpRequest.execute();
    }

    public static <T, R> R formRequest(String url, String method, T formData, Class<R> responseClazz, Map<String, String> headers, String cookie) {
        try(HttpResponse execute = formRequest(url, method, formData, headers, cookie)) {
            String respJson = execute.body();
            // 转换并返回
            return JsonUtil.parse(respJson, responseClazz);
        }
    }

    public static <T> HttpResponse formRequest(String url, String method, T formData, Map<String, String> headers, String cookie) {
        Method requestMethod = Method.valueOf(method.toUpperCase());
        headers = Optional.ofNullable(headers).orElseGet(Map::of);
        return HttpUtil.createRequest(requestMethod, url)
                .headerMap(headers, Boolean.TRUE)
                .form(BeanUtil.beanToMap(formData))
                .cookie(cookie)
                .execute();
    }

}

