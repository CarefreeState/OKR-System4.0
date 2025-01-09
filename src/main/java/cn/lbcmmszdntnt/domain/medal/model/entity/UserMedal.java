package cn.lbcmmszdntnt.domain.medal.model.entity;

import cn.lbcmmszdntnt.common.base.BaseIncrIDEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName user_medal
 */
@TableName(value ="user_medal")
@Data
public class UserMedal extends BaseIncrIDEntity implements Serializable {

    private Long userId;

    private Long medalId;

    private Long credit;

    private Integer level;

    private Boolean isRead;

    private Date issueTime;

    private static final long serialVersionUID = 1L;
}