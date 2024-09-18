package cn.lbcmmszdntnt.util.convert;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.util.StringUtils;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 12:17
 */
public class JsonUtil {

    // 这个转化不能准确转换范型（因为传入泛型类对象无法指定泛型），之前是什么类型的不能准确判断
    // 因为 1 和 1L 若 json 中是 1，默认被认定为 Integer 的 1
    // 而 Integer 不能直接强制类型转换为 Long，就会报错
    // 对于具体的 path 对应的字段的类型，可以使用 analyzeJsonField 携带具体字段类型，即可解析出来
    public static <T> T analyzeJson(String json, Class<T> clazz) {
        return JSONUtil.toBean(json, clazz);
    }

    public static <T> T analyzeJsonField(String json, String path, Class<T> clazz) {
        return JSONUtil.parse(json).getByPath(path, clazz);
    }

    public static <T> String analyzeData(T data) {
        return JSONUtil.parse(data).toStringPretty();
    }

    public static Object analyzeJsonField(String json, String path) {
        return JSONUtil.parse(json).getByPath(path);
    }

    public static <T> String addJsonField(String json, String key, T value) {
        JSON jsonObject = JSONUtil.parse(json);
        jsonObject.putByPath(key, value);
        return jsonObject.toStringPretty();
    }

    public static JsonBuilder jsonBuilder() {
        return new JsonBuilder();
    }

    public static JsonBuilder jsonBuilder(String json) {
        if(!StringUtils.hasText(json)) {
            return new JsonBuilder();
        }
        return new JsonBuilder(json);
    }

    public static class JsonBuilder {

        private final JSON json;

        public <T> JsonBuilder put(String key, T value) {
            this.json.putByPath(key, value);
            return this;
        }

        public String build() {
            return this.json.toStringPretty();
        }

        public JsonBuilder() {
            this.json = new JSONObject();
        }

        public JsonBuilder(String json) {
            this.json = new JSONObject(json);
        }
    }

}
