package cn.bitterfree.domain.media.model.entity;

import cn.bitterfree.common.base.BaseIncrIDEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 资源表
 * @TableName digital_resource
 */
@TableName(value ="digital_resource")
@Data
public class DigitalResource extends BaseIncrIDEntity implements Serializable {

    /**
     * 资源码
     */
    private String code;

    /**
     * 上传时的文件名
     */
    private String originalName;

    /**
     * 在对象存储服务中存储的对象名
     */
    private String fileName;

    /**
     * 最后一次更新时间 update_time 必须在活跃时间限制（距离当前时间 active_limit(ms) 的时间戳内），小于 0 代表不限制
     */
    private Long activeLimit;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}