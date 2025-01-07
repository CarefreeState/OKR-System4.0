package cn.lbcmmszdntnt.domain.record.model.entity;

import cn.lbcmmszdntnt.common.base.BaseIncrIDEntity;
import cn.lbcmmszdntnt.handler.MyBatisJacksonTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName core_recorder
 */
@TableName(value ="core_recorder", autoResultMap = true) // autoResultMap 保证结果有会让 json 映射
@Data
public class CoreRecorder extends BaseIncrIDEntity implements Serializable {

    private Long coreId;

    @TableField(typeHandler = MyBatisJacksonTypeHandler.class) // 将字符串转化为对象保存到属性里，将对象转化为字符串存到表的字段里
    private RecordMap recordMap;

    private static final long serialVersionUID = 1L;
}