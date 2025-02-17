package cn.bitterfree.api.domain.okr.model.entity;

import cn.bitterfree.api.common.base.BaseIncrIDEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName personal_okr
 */
@TableName(value ="personal_okr")
@Schema(description = "个人 OKR")
@Data
public class PersonalOkr extends BaseIncrIDEntity implements Serializable {

    @Schema(description = "内核 ID")
    private Long coreId;

    @Schema(description = "用户 ID")
    private Long userId;

    private static final long serialVersionUID = 1L;
}