package cn.bitterfree.domain.medal.model.converter;

import cn.bitterfree.domain.medal.model.entity.Medal;
import cn.bitterfree.domain.medal.model.vo.UserMedalVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-10-11
 * Time: 20:09
 */
@Mapper
public interface MedalConverter {

    MedalConverter INSTANCE = Mappers.getMapper(MedalConverter.class);

    @Mapping(target = "medalId", source = "id")
    @Mapping(target = "url", source = "greyUrl")
    UserMedalVO medalToUserMedalVO(Medal medal);

}
