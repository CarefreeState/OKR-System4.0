package cn.lbcmmszdntnt.domain.core.model.converter;

import cn.lbcmmszdntnt.domain.core.model.po.inner.KeyResult;
import cn.lbcmmszdntnt.domain.core.model.po.inner.dto.KeyResultDTO;
import cn.lbcmmszdntnt.domain.core.model.po.inner.dto.KeyResultUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-10-11
 * Time: 19:58
 */
@Mapper
public interface KeyResultConverter {

    KeyResultConverter INSTANCE = Mappers.getMapper(KeyResultConverter.class);

    KeyResult keyResultDTOToKeyResult(KeyResultDTO keyResultDTO);

    KeyResult keyResultUpdateDTOToKeyResult(KeyResultUpdateDTO keyResultUpdateDTO);

}
