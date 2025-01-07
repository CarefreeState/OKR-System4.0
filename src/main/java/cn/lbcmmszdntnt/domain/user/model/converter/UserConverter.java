package cn.lbcmmszdntnt.domain.user.model.converter;

import cn.lbcmmszdntnt.domain.user.model.dto.UserinfoDTO;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.domain.user.model.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-10-11
 * Time: 20:17
 */
@Mapper
public interface UserConverter {

    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    UserVO userToUserVO(User user);

    User userinfoDTOToUser(UserinfoDTO userinfoDTO);
}
