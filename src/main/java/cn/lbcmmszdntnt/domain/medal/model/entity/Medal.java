package cn.lbcmmszdntnt.domain.medal.model.entity;

import cn.lbcmmszdntnt.common.base.BaseIncrIDEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName medal
 */
@TableName(value ="medal")
@Data
public class Medal extends BaseIncrIDEntity implements Serializable {

    private String name;

    private String description;

    private String url;

    private String greyUrl;

    private static final long serialVersionUID = 1L;
}