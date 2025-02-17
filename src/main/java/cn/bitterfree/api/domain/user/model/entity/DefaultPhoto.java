package cn.bitterfree.api.domain.user.model.entity;

import cn.bitterfree.api.common.base.BaseIncrIDEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 默认头像表
 * @TableName default_photo
 */
@TableName(value ="default_photo")
@Data
public class DefaultPhoto extends BaseIncrIDEntity implements Serializable {

    /**
     * 资源码
     */
    private String code;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}