package cn.lbcmmszdntnt.domain.core.model.po.quadrant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 21:48
 */
@Schema(description = "初始化第一象限数据")
@Data
public class FirstQuadrantDTO {

    @Schema(description = "第一象限 ID")
    @NotNull(message = "第一象限 ID 不能为空")
    private Long id;

    @Schema(description = "目标")
    @NotBlank(message = "目标不能为空")
    private String objective;

    @Schema(description = "截止时间")
    @NotNull(message = "截止时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deadline;

}
