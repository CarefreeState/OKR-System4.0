package cn.lbcmmszdntnt.domain.core.service.quadrant;


import cn.lbcmmszdntnt.domain.core.model.entity.quadrant.FourthQuadrant;
import cn.lbcmmszdntnt.domain.core.model.vo.quadrant.FourthQuadrantVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 马拉圈
* @description 针对表【fourth_quadrant(第四象限表)】的数据库操作Service
* @createDate 2024-01-20 01:04:21
*/
public interface FourthQuadrantService extends IService<FourthQuadrant> {

    FourthQuadrantVO searchFourthQuadrant(Long coreId);

    Long getFourthQuadrantCoreId(Long id);
}
