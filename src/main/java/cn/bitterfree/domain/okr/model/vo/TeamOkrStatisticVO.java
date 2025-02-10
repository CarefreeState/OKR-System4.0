package cn.bitterfree.domain.okr.model.vo;


import cn.bitterfree.domain.core.model.entity.inner.KeyResult;
import cn.bitterfree.domain.okr.model.entity.TeamOkr;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-26
 * Time: 2:50
 */
@Schema(description = "团队 OKR 统计数据")
@Data
public class TeamOkrStatisticVO extends TeamOkr {

    @Schema(description = "关键结果列表")
    private List<KeyResult> keyResults;

    @Schema(description = "（关键结果完成概率）均值")
    private Double average;

    @Schema(description = "是否完成")
    private Boolean isOver;

    @Schema(description = "完成度")
    private Integer degree;
}
