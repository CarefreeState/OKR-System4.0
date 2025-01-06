package cn.lbcmmszdntnt.domain.media.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 资源表
 * @TableName digital_resource
 */
@TableName(value ="digital_resource")
@Data
public class DigitalResource implements Serializable {
    /**
     * 资源 id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 资源码
     */
    private String code;

    /**
     * 资源类型
     */
    private String type;

    /**
     * 上传时的文件名
     */
    private String originalName;

    /**
     * 在对象存储服务中存储的对象名
     */
    private String fileName;

    /**
     * 乐观锁
     */
    private Integer version;

    /**
     * 伪删除标记
     */
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}