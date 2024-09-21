package cn.lbcmmszdntnt.util.web;


import cn.hutool.http.HttpRequest;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class HttpUtil {

    private final static String JSON_CONTENT_TYPE = "application/json; charset=utf-8";

    private static final String NOT_DIGIT_PATTERN = "[^0-9]+";

    public static String getFormBody(Map<String, Object> map) {
        if (Objects.isNull(map)) {
            return "";
        }
        try {
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String keyVale = String.format("%s=%s", entry.getKey(), URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                builder.append(keyVale);
                builder.append("&");
            }
            if (StringUtils.hasLength(builder)) {
                builder.deleteCharAt(builder.length() - 1);
            }
            return builder.toString();
        } catch (UnsupportedEncodingException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

    public static String getQueryString(Map<String, Object> map) {
        String formBody = getFormBody(map);
        if (StringUtils.hasLength(formBody)) {
            return "?" + formBody;
        } else {
            return "";
        }
    }

    public static String doGet(String httpUrl) {
        return doGet(httpUrl, null);
    }

    public static String doGet(String httpUrl, Map<String, Object> map) {
        // 有queryString的就加
        httpUrl += HttpUtil.getQueryString(map);
        return HttpRequest.get(httpUrl)
                .execute()
                .body();
    }

    public static String doPostFrom(String httpUrl, Map<String, Object> map) {
        return HttpRequest.post(httpUrl)
                .form(map)
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

    public static boolean isInvalidIpAddress(String ipAddress) {
        return !StringUtils.hasText(ipAddress) || "unknown".equalsIgnoreCase(ipAddress);
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

    public static String getIPAddress() {
        return Optional.ofNullable(getRequest())
                .map(request -> {
                    String ipAddress = request.getHeader("X-Forwarded-For");
                    if (isInvalidIpAddress(ipAddress)) {
                        ipAddress = request.getHeader("Proxy-Client-IP");
                    }
                    if (isInvalidIpAddress(ipAddress)) {
                        ipAddress = request.getHeader("WL-Proxy-Client-IP");
                    }
                    if (isInvalidIpAddress(ipAddress)) {
                        ipAddress = request.getRemoteAddr();
                    }
                    return ipAddress;
                }).orElse("");
    }

    public static String getDigitIP() {
        return getIPAddress().replaceAll(NOT_DIGIT_PATTERN, "");
    }

}

