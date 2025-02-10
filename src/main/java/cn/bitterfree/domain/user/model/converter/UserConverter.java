package cn.bitterfree.domain.user.model.converter;

import cn.bitterfree.common.base.BasePageResult;
import cn.bitterfree.domain.user.enums.UserType;
import cn.bitterfree.domain.user.model.dto.UserinfoDTO;
import cn.bitterfree.domain.user.model.entity.User;
import cn.bitterfree.domain.user.model.vo.UserQueryVO;
import cn.bitterfree.domain.user.model.vo.UserTypeVO;
import cn.bitterfree.domain.user.model.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

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

    List<UserTypeVO> userTypeListToUserTypeVOList(List<UserType> userTypeList);

    User userinfoDTOToUser(UserinfoDTO userinfoDTO);

    UserQueryVO userBasePageResultToUserQueryVO(BasePageResult<User> userBasePageResult);
}
