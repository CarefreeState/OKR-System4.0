package cn.bitterfree.api.domain.user.service.impl;

import cn.bitterfree.api.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.common.util.juc.threadpool.IOThreadPool;
import cn.bitterfree.api.domain.media.service.FileMediaService;
import cn.bitterfree.api.domain.user.enums.UserType;
import cn.bitterfree.api.domain.user.model.converter.UserConverter;
import cn.bitterfree.api.domain.user.model.dto.UserQueryDTO;
import cn.bitterfree.api.domain.user.model.dto.UserinfoDTO;
import cn.bitterfree.api.domain.user.model.entity.User;
import cn.bitterfree.api.domain.user.model.mapper.UserMapper;
import cn.bitterfree.api.domain.user.model.vo.UserQueryVO;
import cn.bitterfree.api.domain.user.service.UserService;
import cn.bitterfree.api.redis.cache.RedisCache;
import cn.bitterfree.api.redis.lock.RedisLock;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cn.bitterfree.api.domain.user.constants.UserConstants.*;

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

    private final FileMediaService fileMediaService;

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
        userQueryDTO = Optional.ofNullable(userQueryDTO).orElseGet(UserQueryDTO::new);
        userQueryDTO.init();
        // 分页
        UserQueryVO userQueryVO = userMapper.queryUser(userQueryDTO);
        // 封装
        Long pageSize = userQueryVO.getPageSize();
        userQueryVO.setPages(pageSize.equals(0L) ? 0L : Math.ceilDiv(userQueryVO.getTotal(), pageSize)); // ceilDiv 向上取整的除法
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

    @Override
    public void mergeUser(User mainUser, User user) {
        if (!StringUtils.hasText(mainUser.getEmail())) {
            mainUser.setEmail(user.getEmail());
        } else if (StringUtils.hasText(user.getEmail())) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_ACCOUNT_MERGE_CONFLICT);
        }
        if (!StringUtils.hasText(mainUser.getOpenid())) {
            mainUser.setOpenid(user.getOpenid());
        } else if (StringUtils.hasText(user.getOpenid())) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_ACCOUNT_MERGE_CONFLICT);
        }
        if (!StringUtils.hasText(mainUser.getUnionid())) {
            mainUser.setUnionid(user.getUnionid());
        } else if (StringUtils.hasText(user.getUnionid())) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_ACCOUNT_MERGE_CONFLICT);
        }
        if (!StringUtils.hasText(mainUser.getPhone())) {
            mainUser.setPhone(user.getPhone());
        } else if (StringUtils.hasText(user.getPhone())) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_ACCOUNT_MERGE_CONFLICT);
        }
        if (!StringUtils.hasText(mainUser.getPassword())) {
            mainUser.setPassword(user.getPassword());
        }
        this.updateById(mainUser);
    }

    @Override
    public List<String> mergeUser(Long mainUserId, Long userId) {
        return getUserById(userId).map(user -> {
            return getUserById(mainUserId).map(mainUser -> {
                // 1. 迁移账号信息（如果被合并掉的用户，也绑定了多种信息）
                mergeUser(mainUser, user);
                // 2. 删除用户
                log.warn("删除用户 {}", userId);
                this.removeById(userId);
                IOThreadPool.submit(() -> {
                    // 3. 删除头像
                    fileMediaService.remove(user.getPhoto());
                });
                return List.of(ID_USER_MAP + user.getId(), USERNAME_USER_MAP + user.getUsername(),
                        EMAIL_USER_MAP + user.getEmail(), WX_USER_MAP + user.getOpenid());
            }).orElseGet(ArrayList::new);
        }).orElseGet(ArrayList::new);
    }

}