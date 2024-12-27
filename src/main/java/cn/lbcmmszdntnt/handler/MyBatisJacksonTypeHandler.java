package cn.lbcmmszdntnt.handler;


import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.util.convert.JsonUtil;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.util.Optional;

@MappedTypes({Object.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
@Slf4j
public class MyBatisJacksonTypeHandler extends AbstractJsonTypeHandler<Object> {

    private final Class<?> type;

    public MyBatisJacksonTypeHandler(Class<?> type) {
        super(type);
        if (log.isTraceEnabled()) {
            log.trace("MyBatisJacksonTypeHandler({})", type);
        }
        Optional.ofNullable(type).orElseThrow(() ->
                new GlobalServiceException("Type argument cannot be null"));
        this.type = type;
    }

    @Override
    public Object parse(String json) {
        return JsonUtil.parse(json, this.type);
    }

    @Override
    public String toJson(Object obj) {
        return JsonUtil.toJson(obj);
    }
}
