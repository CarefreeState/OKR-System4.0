package cn.lbcmmszdntnt.domain.user.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.email.service.EmailService;
import cn.lbcmmszdntnt.domain.email.util.IdentifyingCodeValidator;
import cn.lbcmmszdntnt.domain.qrcode.constants.QRCodeConstants;
import cn.lbcmmszdntnt.domain.user.model.converter.UserConverter;
import cn.lbcmmszdntnt.domain.user.model.dto.UserinfoDTO;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.domain.user.model.mapper.UserMapper;
import cn.lbcmmszdntnt.domain.user.service.UserService;
import cn.lbcmmszdntnt.domain.user.service.WxBindingService;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.jwt.util.JwtUtil;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import cn.lbcmmszdntnt.wxtoken.model.dto.JsCode2SessionDTO;
import cn.lbcmmszdntnt.wxtoken.model.vo.JsCode2SessionVO;
import cn.lbcmmszdntnt.wxtoken.util.WxHttpRequestUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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

    private final static String JWT_SUBJECT = "微信登录确认";

    private final static String EMAIL_USER_MAP = "emailUserMap:";

    private final static String WX_USER_MAP = "wxUserMap:";

    private final static String ID_USER_MAP = "idUserMap:";

    private final static Long EMAIL_USER_TTL = 2L;

    private final static Long WX_USER_TTL = 2L;

    private final static Long ID_USER_TTL = 2L;

    private final static TimeUnit EMAIL_USER_UNIT = TimeUnit.HOURS;

    private final static TimeUnit WX_USER_UNIT = TimeUnit.HOURS;

    private final static TimeUnit ID_USER_UNIT = TimeUnit.HOURS;

    private final RedisCache redisCache;

    private final WxBindingService wxBindingService;

    private final EmailService emailService;

    @Override
    public JsCode2SessionVO getUserFlag(String code) {
        return WxHttpRequestUtil.jsCode2Session(JsCode2SessionDTO.builder().jsCode(code).build());
    }

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
    public void deleteUserIdCache(Long id) {
        redisCache.deleteObject(ID_USER_MAP + id);
    }

    @Override
    public void deleteUserEmailCache(String email) {
        redisCache.deleteObject(EMAIL_USER_MAP + email);
    }

    @Override
    public void deleteUserOpenidCache(String openid) {
        redisCache.deleteObject(WX_USER_MAP + openid);
    }

    @Override
    public void deleteUserAllCache(Long id) {
        getUserById(id).ifPresent(user -> {
            deleteUserEmailCache(user.getEmail());
            deleteUserOpenidCache(user.getOpenid());
        });
        deleteUserIdCache(id);
    }

    @Override
    public void improveUserinfo(UserinfoDTO userinfoDTO, Long userId) {
        User updateUser = UserConverter.INSTANCE.userinfoDTOToUser(userinfoDTO);
        // 修改
        this.lambdaUpdate().eq(User::getId, userId).update(updateUser);
        deleteUserAllCache(userId);
    }

    @Override
    public void bindingEmail(Long userId, String email, String code, String recordEmail) {
        // 检查验证码
        emailService.checkIdentifyingCode(IdentifyingCodeValidator.EMAIL_BINDING, email, code);
        // 判断邮箱用户是否存在
        getUserByEmail(email).ifPresent(user -> {
            throw new GlobalServiceException(GlobalServiceStatusCode.EMAIL_USER_BE_BOUND);
        });
        this.lambdaUpdate()
                .eq(User::getId, userId)
                .set(User::getEmail, email)
                .update();
        deleteUserAllCache(userId);
        if (StringUtils.hasText(recordEmail)) {
            deleteUserEmailCache(recordEmail);
        }
        log.info("用户 {} 成功绑定 邮箱 {}", userId, email);
    }

    private Optional<String> getOpenidByUserId(Long id) {
        return getUserById(id).map(User::getOpenid);
    }

    @Override
    public void bindingWx(Long userId, String randomCode, String code) {
        // 验证以下验证码
        wxBindingService.checkSecret(userId, randomCode);
        JsCode2SessionVO userFlag = getUserFlag(code);
        String openid = userFlag.getOpenid();
        String unionid = userFlag.getUnionid();
        if(Objects.isNull(openid)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.WX_CODE_NOT_VALID);
        }
        // 查询 openid 是否被注册过
        getUserByOpenid(openid).ifPresent(user -> {
            throw new GlobalServiceException(GlobalServiceStatusCode.WX_USER_BE_BOUND);
        });
        // 判断当前用户是否绑定了微信
        // todo: 避免混乱所以现在暂且不支持微信重新绑定，之后需要再说
        getOpenidByUserId(userId).ifPresent(openidByUserId -> {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_BOUND_WX);
        });
        this.lambdaUpdate()
                .eq(User::getId, userId)
                .set(User::getOpenid, openid)
                .update();
        deleteUserAllCache(userId);
        log.info("用户 {} 成功绑定 微信 {}", userId, openid);
    }

    @Override
    public void onLoginState(String secret, Long userId) {
        String redisKey = QRCodeConstants.WX_LOGIN_QR_CODE_MAP + secret;
        redisCache.getObject(redisKey, Long.class).ifPresentOrElse(uid -> {
            if (uid.compareTo(0L) <= 0) {
                redisCache.setObject(redisKey, JwtUtil.createJwt(JWT_SUBJECT, userId),
                        QRCodeConstants.WX_LOGIN_QR_CODE_TTL, QRCodeConstants.WX_LOGIN_QR_CODE_UNIT);
            }
        }, () -> {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_LOGIN_CODE_VALID);
        });
    }

}