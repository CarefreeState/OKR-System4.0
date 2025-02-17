package cn.bitterfree.api.domain.user.model.converter;

import cn.bitterfree.api.common.base.BasePageResult;
import cn.bitterfree.api.domain.user.enums.UserType;
import cn.bitterfree.api.domain.user.model.dto.UserinfoDTO;
import cn.bitterfree.api.domain.user.model.entity.User;
import cn.bitterfree.api.domain.user.model.vo.UserQueryVO;
import cn.bitterfree.api.domain.user.model.vo.UserTypeVO;
import cn.bitterfree.api.domain.user.model.vo.UserVO;
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
