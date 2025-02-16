package cn.bitterfree.domain.user.model.mapper;

import cn.bitterfree.domain.user.enums.UserType;
import cn.bitterfree.domain.user.model.entity.User;
import cn.bitterfree.domain.user.model.vo.UserQueryVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author 马拉圈
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2024-01-22 14:18:10
*/
public interface UserMapper extends BaseMapper<User> {

    UserQueryVO queryUser(@Param("username") String username, @Param("nickname") String nickname, @Param("userType") UserType userType,
                          @Param("limit") Long limit, @Param("offset") Long offset);

}




