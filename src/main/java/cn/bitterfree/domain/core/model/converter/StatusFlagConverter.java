package cn.bitterfree.domain.core.model.converter;

import cn.bitterfree.domain.core.model.dto.inner.StatusFlagDTO;
import cn.bitterfree.domain.core.model.dto.inner.StatusFlagUpdateDTO;
import cn.bitterfree.domain.core.model.entity.inner.StatusFlag;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-10-11
 * Time: 20:03
 */
@Mapper
public interface StatusFlagConverter {

    StatusFlagConverter INSTANCE = Mappers.getMapper(StatusFlagConverter.class);

    StatusFlag statusFlagDTOToStatusFlag(StatusFlagDTO statusFlagDTO);

    StatusFlag statusFlagUpdateDTOToStatusFlag(StatusFlagUpdateDTO statusFlagUpdateDTO);

}
