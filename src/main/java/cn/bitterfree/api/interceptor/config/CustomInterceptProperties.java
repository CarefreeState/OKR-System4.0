package cn.bitterfree.api.interceptor.config;

import lombok.Data;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-09
 * Time: 15:04
 */
@Data
public class CustomInterceptProperties {

    private List<String> urls;

    private InterceptProperties properties;

}
