package cn.lbcmmszdntnt.domain.record.model.po;

import cn.lbcmmszdntnt.handler.MyBatisJacksonTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName core_recorder
 */
@TableName(value ="core_recorder", autoResultMap = true) // autoResultMap 保证结果有会让 json 映射
@Data
public class CoreRecorder implements Serializable {
    private Long id;

    private Long coreId;

    @TableField(typeHandler = MyBatisJacksonTypeHandler.class) // 将字符串转化为对象保存到属性里，将对象转化为字符串存到表的字段里
    private RecordMap recordMap;

    private Integer version;

    private Boolean isDeleted;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}