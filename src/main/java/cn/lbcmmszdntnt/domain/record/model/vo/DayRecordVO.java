package cn.lbcmmszdntnt.domain.record.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
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

    @SchemaProperty(name = "ID")
    private Long id;

    @SchemaProperty(name = "日期")
    private Date recordDate;

    @SchemaProperty(name = "信心指数平均值")
    private Double credit1;

    @SchemaProperty(name = "第二象限任务完成数")
    private Integer credit2;

    @SchemaProperty(name = "第三象限任务完成数")
    private Integer credit3;

    @SchemaProperty(name = "状态指标评估值")
    private Integer credit4;

}
