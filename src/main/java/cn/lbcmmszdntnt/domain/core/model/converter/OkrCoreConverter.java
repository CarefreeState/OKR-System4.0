package cn.lbcmmszdntnt.domain.core.model.converter;

import cn.lbcmmszdntnt.domain.core.model.po.OkrCore;
import cn.lbcmmszdntnt.domain.core.model.vo.OkrCoreVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-10-11
 * Time: 20:08
 */
@Mapper
public interface OkrCoreConverter {

    OkrCoreConverter INSTANCE = Mappers.getMapper(OkrCoreConverter.class);

    OkrCoreVO okrCoreToOkrCoreVO(OkrCore okrCore);
}
