package cn.lbcmmszdntnt.domain.core.model.mapper.quadrant;

import cn.lbcmmszdntnt.domain.core.model.po.quadrant.ThirdQuadrant;
import cn.lbcmmszdntnt.domain.core.model.po.quadrant.vo.ThirdQuadrantVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
* @author 马拉圈
* @description 针对表【third_quadrant(第三象限表)】的数据库操作Mapper
* @createDate 2024-01-20 01:04:20
* @Entity com.macaku.core.domain.po.quadrant.ThirdQuadrant
*/
public interface ThirdQuadrantMapper extends BaseMapper<ThirdQuadrant> {

    Optional<ThirdQuadrantVO> searchThirdQuadrant(@Param("coreId") Long coreId);

}



