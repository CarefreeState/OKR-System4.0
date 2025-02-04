package cn.lbcmmszdntnt.domain.core.model.mapper.quadrant;


import cn.lbcmmszdntnt.domain.core.model.entity.quadrant.FirstQuadrant;
import cn.lbcmmszdntnt.domain.core.model.vo.quadrant.FirstQuadrantVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
* @author 马拉圈
* @description 针对表【first_quadrant(第一象限表)】的数据库操作Mapper
* @createDate 2024-01-20 01:04:21
*/
public interface FirstQuadrantMapper extends BaseMapper<FirstQuadrant> {

    Optional<FirstQuadrantVO> searchFirstQuadrant(@Param("coreId") Long coreId);

}




