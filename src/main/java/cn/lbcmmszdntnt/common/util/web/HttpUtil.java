package cn.lbcmmszdntnt.common.util.web;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import cn.lbcmmszdntnt.common.util.convert.JsonUtil;
import cn.lbcmmszdntnt.common.util.convert.ObjectUtil;
import cn.lbcmmszdntnt.common.util.media.MediaUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class HttpUtil {

    private final static String JSON_CONTENT_TYPE = "application/json; charset=utf-8";
    public final static Map<String, String> JSON_CONTENT_TYPE_HEADER = Map.of(HttpHeaders.CONTENT_TYPE, JSON_CONTENT_TYPE);

    public final static PathMatcher PATH_MATCHER = new AntPathMatcher();

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

    public static String getBaseUrl(HttpServletRequest request, String... uris) {
        String uri = Arrays.stream(uris).filter(StringUtils::hasText).collect(Collectors.joining());
        return getHost(request) + uri;
    }

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

    public static String doGet(String httpUrl, Map<String, List<String>> params) {
        return HttpRequest.get(buildUrl(httpUrl, params))
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

    public static <R, T> HttpRequest getJsonRequest(String url, String method, T requestBody, Map<String, String> headers) {
        // 准备参数
        Method requestMethod = Method.valueOf(method.toUpperCase());
        headers = Optional.ofNullable(headers).orElseGet(Map::of);
        // 发出请求
        HttpRequest httpRequest = cn.hutool.http.HttpUtil.createRequest(requestMethod, url)
                .headerMap(headers, Boolean.TRUE)
                .headerMap(JSON_CONTENT_TYPE_HEADER, Boolean.TRUE);
        if(Objects.nonNull(requestBody)) {
            String reqJson = JsonUtil.toJson(requestBody);
            httpRequest = httpRequest.body(reqJson);
        }
        return httpRequest;
    }

    public static <R, T> R jsonRequest(String url, String method, T requestBody, Class<R> responseClazz, Map<String, String> headers) {
        String respJson = getJsonRequest(url, method, requestBody, headers).execute().body();
        // 转换并返回
        return JsonUtil.parse(respJson, responseClazz);
    }

    public static <T> byte[] jsonRequest(String url, String method, T requestBody, Map<String, String> headers) {
        return getJsonRequest(url, method, requestBody, headers).execute().bodyBytes();
    }

}

