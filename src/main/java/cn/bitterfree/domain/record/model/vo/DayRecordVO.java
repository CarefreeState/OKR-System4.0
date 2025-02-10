package cn.bitterfree.domain.record.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-21
 * Time: 4:24
 */
@Schema(description = "日记录数据")
@Data
public class DayRecordVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "日期")
    private Date recordDate;

    @Schema(description = "信心指数平均值")
    private Double credit1;

    @Schema(description = "第二象限任务完成数")
    private Integer credit2;

    @Schema(description = "第三象限任务完成数")
    private Integer credit3;

    @Schema(description = "状态指标评估值")
    private Integer credit4;

}
