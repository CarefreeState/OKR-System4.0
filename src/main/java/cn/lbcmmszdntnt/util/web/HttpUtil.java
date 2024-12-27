package cn.lbcmmszdntnt.util.web;


import cn.hutool.http.HttpRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
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

}

