package cn.bitterfree.api.domain.medal.model.converter;

import cn.bitterfree.api.domain.medal.model.entity.Medal;
import cn.bitterfree.api.domain.medal.model.entity.UserMedal;
import cn.bitterfree.api.domain.medal.model.vo.UserMedalVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-10-11
 * Time: 20:13
 */
@Mapper
public interface UserMedalConverter {

    UserMedalConverter INSTANCE = Mappers.getMapper(UserMedalConverter.class);

    UserMedalVO userMedalToUserMedalVO(UserMedal userMedal);

    UserMedalVO medalMapToUserMedalVO(Medal medal, @MappingTarget UserMedalVO userMedalVO);

    UserMedalVO userMedalMapToUserMedalVO(UserMedal userMedal, @MappingTarget UserMedalVO userMedalVO);
}
