package cn.lbcmmszdntnt.domain.medal.model.converter;

import cn.lbcmmszdntnt.domain.medal.model.po.Medal;
import cn.lbcmmszdntnt.domain.medal.model.po.UserMedal;
import cn.lbcmmszdntnt.domain.medal.model.vo.UserMedalVO;
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
public interface UserMalConverter {

    UserMalConverter INSTANCE = Mappers.getMapper(UserMalConverter.class);

    UserMedalVO userMedalToUserMedalVO(UserMedal userMedal);

    UserMedalVO medalMapToUserMedalVO(Medal medal, @MappingTarget UserMedalVO userMedalVO);

    UserMedalVO userMedalMapToUserMedalVO(UserMedal userMedal, @MappingTarget UserMedalVO userMedalVO);
}
