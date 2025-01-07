package cn.lbcmmszdntnt.domain.core.model.converter;

import cn.lbcmmszdntnt.domain.core.model.dto.quadrant.FirstQuadrantDTO;
import cn.lbcmmszdntnt.domain.core.model.entity.quadrant.FirstQuadrant;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-10-11
 * Time: 20:06
 */
@Mapper
public interface FirstQuadrantConverter {

    FirstQuadrantConverter INSTANCE = Mappers.getMapper(FirstQuadrantConverter.class);

    FirstQuadrant firstQuadrantDTOToFirstQuadrant(FirstQuadrantDTO firstQuadrantDTO);

}
