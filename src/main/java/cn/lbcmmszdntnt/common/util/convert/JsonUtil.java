package cn.lbcmmszdntnt.common.util.convert;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.Date;

import static cn.lbcmmszdntnt.common.constants.DateTimeConstants.*;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 12:17
 */
public class JsonUtil {

    public final static ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new Jackson2ObjectMapperBuilder()
                .indentOutput(Boolean.TRUE)
                .dateFormat(DATE_FORMAT)
                .simpleDateFormat(DATE_TIME_PATTERN)
                .serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_PATTERN)))
                .deserializers(new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
                .deserializers(new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_PATTERN)))
                .modulesToInstall(new ParameterNamesModule())
                .build()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.FALSE)
        ;
    }

    public static void main(String[] args) {
        User user = new User();
        user.setId(1L);
        user.setCreateTime(new Date());
        String json = toJson(user);
        System.out.println(json);
        User parse = parse(json, User.class);
        System.out.println(parse);
    }

    public static <T> T parse(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

    public static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

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
