package cn.bitterfree.domain.user.service.impl;

import cn.bitterfree.common.base.BasePageQuery;
import cn.bitterfree.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.common.exception.GlobalServiceException;
import cn.bitterfree.domain.user.enums.UserType;
import cn.bitterfree.domain.user.model.converter.UserConverter;
import cn.bitterfree.domain.user.model.dto.UserQueryDTO;
import cn.bitterfree.domain.user.model.dto.UserinfoDTO;
import cn.bitterfree.domain.user.model.entity.User;
import cn.bitterfree.domain.user.model.mapper.UserMapper;
import cn.bitterfree.domain.user.model.vo.UserQueryVO;
import cn.bitterfree.domain.user.service.UserService;
import cn.bitterfree.redis.cache.RedisCache;
import cn.bitterfree.redis.lock.RedisLock;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Optional;

import static cn.bitterfree.domain.user.constants.UserConstants.*;

/**
* @author 马拉圈
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-01-22 14:18:10
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    private final RedisLock redisLock;

    private final RedisCache redisCache;

    private final UserMapper userMapper;

    @Override
    public Optional<User> getUserById(Long id) {
        String redisKey = ID_USER_MAP + id;
        return Optional.ofNullable(redisCache.getObject(redisKey, User.class).orElseGet(() -> {
            return this.lambdaQuery().eq(User::getId, id).oneOpt().map(user -> {
                redisCache.setObject(redisKey, user, ID_USER_TTL, ID_USER_UNIT);
                return user;
            }).orElse(null);
        }));
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        String redisKey = USERNAME_USER_MAP + username;
        return Optional.ofNullable(redisCache.getObject(redisKey, User.class).orElseGet(() -> {
            return this.lambdaQuery().eq(User::getUsername, username).oneOpt().map(user -> {
                redisCache.setObject(redisKey, user, USERNAME_USER_TTL, USERNAME_USER_UNIT);
                return user;
            }).orElse(null);
        }));
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        String redisKey = EMAIL_USER_MAP + email;
        return Optional.ofNullable(redisCache.getObject(redisKey, User.class).orElseGet(() -> {
            return this.lambdaQuery().eq(User::getEmail, email).oneOpt().map(user -> {
                redisCache.setObject(redisKey, user, EMAIL_USER_TTL, EMAIL_USER_UNIT);
                return user;
            }).orElse(null);
        }));
    }

    @Override
    public Optional<User> getUserByOpenid(String openid) {
        String redisKey = WX_USER_MAP + openid;
        return Optional.ofNullable(redisCache.getObject(redisKey, User.class).orElseGet(() -> {
            return this.lambdaQuery().eq(User::getOpenid, openid).oneOpt().map(user -> {
                redisCache.setObject(redisKey, user, WX_USER_TTL, WX_USER_UNIT);
                return user;
            }).orElse(null);
        }));
    }

    @Override
    public User checkAndGetUserByUsername(String username) {
        return getUserByUsername(username).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.USER_USERNAME_PASSWORD_ERROR));
    }

    @Override
    public User registerUser(User user) {
        log.info("新用户注册 -> {}", user);
        // 保证 username 唯一性
        String username = Optional.ofNullable(user).map(User::getUsername).filter(StringUtils::hasText).orElseThrow(() ->
                new GlobalServiceException("用户名为空，无法注册", GlobalServiceStatusCode.PARAM_IS_BLANK));
        return redisLock.tryLockGetSomething(EXISTS_USER_USERNAME_LOCK + user.getUsername(), () -> {
            getUserByUsername(username).ifPresentOrElse(dbUser -> {
                throw new GlobalServiceException(String.format("用户名 %s 已存在", username), GlobalServiceStatusCode.USER_ACCOUNT_ALREADY_EXIST);
            }, () -> {
                this.save(user);
            });
            return user;
        }, () -> null);
    }

    @Override
    public void clearUserAllCache(Long id) {
        // 直接查数据库
        lambdaQuery().eq(User::getId, id).oneOpt().ifPresent(user -> {
            redisCache.deleteObject(ID_USER_MAP + user.getId());
            redisCache.deleteObject(USERNAME_USER_MAP + user.getUsername());
            redisCache.deleteObject(EMAIL_USER_MAP + user.getEmail());
            redisCache.deleteObject(WX_USER_MAP + user.getOpenid());
        });
    }

    @Override
    public void improveUserinfo(UserinfoDTO userinfoDTO, Long userId) {
        User updateUser = UserConverter.INSTANCE.userinfoDTOToUser(userinfoDTO);
        // 修改
        this.lambdaUpdate().eq(User::getId, userId).update(updateUser);
        clearUserAllCache(userId);
    }

//    @Override
//    public UserQueryVO queryUser(UserQueryDTO userQueryDTO) {
//        // 解析分页参数获取 page
//        IPage<User> page = null;
//        String username = null;
//        String nickname = null;
//        UserType userType = null;
//        // 获取条件分页查询参数
//        if(Objects.nonNull(userQueryDTO)) {
//            page = userQueryDTO.toMpPage();
//            username = userQueryDTO.getUsername();
//            nickname = userQueryDTO.getNickname();
//            userType = userQueryDTO.getUserType();
//        } else {
//            page = new BasePageQuery().toMpPage();
//        }
//        // 分页
//        IPage<User> userIPage = this.lambdaQuery()
//                .like(StringUtils.hasText(username), User::getUsername, username)
//                .like(StringUtils.hasText(nickname), User::getNickname, nickname)
//                .eq(Objects.nonNull(userType), User::getUserType, userType)
//                .page(page);
//        // 封装
//        BasePageResult<User> userBasePageResult = BasePageResult.of(userIPage);
//        // 转化并返回
//        return UserConverter.INSTANCE.userBasePageResultToUserQueryVO(userBasePageResult);
//    }

    // 覆盖索引 + 子查询实现分页查询
    @Override
    public UserQueryVO queryUser(UserQueryDTO userQueryDTO) {
        // 解析分页参数获取 page
        IPage<User> page = null;
        String username = null;
        String nickname = null;
        UserType userType = null;
        // 获取条件分页查询参数
        if (Objects.nonNull(userQueryDTO)) {
            page = userQueryDTO.toMpPage();
            username = userQueryDTO.getUsername();
            nickname = userQueryDTO.getNickname();
            userType = userQueryDTO.getUserType();
        } else {
            page = new BasePageQuery().toMpPage();
        }
        // 分页
        long size = page.getSize();
        long current = page.getCurrent();
        UserQueryVO userQueryVO = userMapper.queryUser(username, nickname, userType, size, size * (current - 1));
        // 封装
        userQueryVO.setPageSize(size);
        userQueryVO.setCurrent(current);
        userQueryVO.setPages(size == 0L ? 0L : Math.ceilDiv(userQueryVO.getTotal(), size)); // ceilDiv 向上取整的除法
        // 转化并返回
        return userQueryVO;
    }

    @Override
    public void updateUserType(Long userId, UserType userType) {
        User updateUser = new User();
        updateUser.setUserType(userType);
        this.lambdaUpdate().eq(User::getId, userId).update(updateUser);
        clearUserAllCache(userId);
    }

}