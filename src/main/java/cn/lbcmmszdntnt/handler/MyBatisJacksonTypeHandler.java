package cn.lbcmmszdntnt.handler;


import cn.lbcmmszdntnt.exception.GlobalServiceException;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.io.IOException;
import java.util.Optional;

@MappedTypes({Object.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
@Slf4j
public class MyBatisJacksonTypeHandler extends AbstractJsonTypeHandler<Object> {

    private final static ObjectMapper OBJECT_MAPPER;

    private final Class<?> type;

    static {
        // 忽略未定义的属性
        OBJECT_MAPPER = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.FALSE);
    }

    public MyBatisJacksonTypeHandler(Class<?> type) {
        if (log.isTraceEnabled()) {
            log.trace("MyBatisJacksonTypeHandler({})", type);
        }
        Optional.ofNullable(type).orElseThrow(() ->
                new GlobalServiceException("Type argument cannot be null"));
        this.type = type;
    }

    @Override
    public Object parse(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, this.type);
        } catch (IOException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

    @Override
    public String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }
}
