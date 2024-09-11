package cn.lbcmmszdntnt.domain.core.model.vo;


import cn.lbcmmszdntnt.domain.core.model.po.OkrCore;
import cn.lbcmmszdntnt.domain.core.model.po.quadrant.vo.FirstQuadrantVO;
import cn.lbcmmszdntnt.domain.core.model.po.quadrant.vo.FourthQuadrantVO;
import cn.lbcmmszdntnt.domain.core.model.po.quadrant.vo.SecondQuadrantVO;
import cn.lbcmmszdntnt.domain.core.model.po.quadrant.vo.ThirdQuadrantVO;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 2:38
 */
@Schema(description = "OKR 内核详细数据")
@Data
public class OkrCoreVO extends OkrCore {

    @SchemaProperty(name = "第一象限详细信息")
    private FirstQuadrantVO firstQuadrantVO;

    @SchemaProperty(name = "第二象限详细信息")
    private SecondQuadrantVO secondQuadrantVO;

    @SchemaProperty(name = "第三象限详细信息")
    private ThirdQuadrantVO thirdQuadrantVO;

    @SchemaProperty(name = "第四象限详细信息")
    private FourthQuadrantVO fourthQuadrantVO;

}
