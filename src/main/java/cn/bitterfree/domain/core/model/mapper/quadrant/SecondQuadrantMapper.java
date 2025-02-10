package cn.bitterfree.domain.core.model.mapper.quadrant;

import cn.bitterfree.domain.core.model.entity.quadrant.SecondQuadrant;
import cn.bitterfree.domain.core.model.vo.quadrant.SecondQuadrantVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
* @author 马拉圈
* @description 针对表【second_quadrant(第二象限表)】的数据库操作Mapper
* @createDate 2024-01-20 01:04:21
*/
public interface SecondQuadrantMapper extends BaseMapper<SecondQuadrant> {

    Optional<SecondQuadrantVO> searchSecondQuadrant(@Param("coreId") Long coreId);

}




