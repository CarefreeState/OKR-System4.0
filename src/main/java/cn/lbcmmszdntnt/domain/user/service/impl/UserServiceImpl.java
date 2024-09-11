package cn.lbcmmszdntnt.domain.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.lbcmmszdntnt.common.constants.SuppressWarningsValue;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.config.StaticMapperConfig;
import cn.lbcmmszdntnt.domain.email.factory.EmailServiceFactory;
import cn.lbcmmszdntnt.domain.qrcode.config.QRCodeConfig;
import cn.lbcmmszdntnt.domain.qrcode.service.WxBindingQRCodeService;
import cn.lbcmmszdntnt.domain.user.model.dto.UserinfoDTO;
import cn.lbcmmszdntnt.domain.user.model.mapper.UserMapper;
import cn.lbcmmszdntnt.domain.user.model.po.User;
import cn.lbcmmszdntnt.domain.user.service.UserService;
import cn.lbcmmszdntnt.domain.user.util.ExtractUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.redis.RedisCache;
import cn.lbcmmszdntnt.redis.RedisLock;
import cn.lbcmmszdntnt.util.convert.JsonUtil;
import cn.lbcmmszdntnt.util.jwt.JwtUtil;
import cn.lbcmmszdntnt.util.media.MediaUtil;
import cn.lbcmmszdntnt.util.thread.pool.IOThreadPool;
import cn.lbcmmszdntnt.util.web.HttpUtil;
import cn.lbcmmszdntnt.wxtoken.TokenUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
* @author 马拉圈
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-01-22 14:18:10
*/
@Service
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings(value = {SuppressWarningsValue.SPRING_JAVA_INJECTION_POINT_AUTOWIRING_INSPECTION})
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    private final static String EMAIL_USER_MAP = "emailUserMap:";

    private final static String WX_USER_MAP = "wxUserMap:";

    private final static String USERID_OPENID_MAP = "useridOpenidMap:";

    private final static String USER_PHOTO_LOCK = "userPhotoLock:";

    private final static Long EMAIL_USER_TTL = 2L;

    private final static Long WX_USER_TTL = 2L;

    private final static Long USERID_OPENID_TTL = 2L;

    private final static TimeUnit EMAIL_USER_UNIT = TimeUnit.HOURS;

    private final static TimeUnit WX_USER_UNIT = TimeUnit.HOURS;

    private final static TimeUnit USERID_OPENID_UNIT = TimeUnit.HOURS;

    @Value("${spring.redisson.timeout}")
    private Long timeout; // 秒

    private final RedisCache redisCache;

    private final RedisLock redisLock;

    private final EmailServiceFactory emailServiceFactory;

    private final WxBindingQRCodeService wxBindingQRCodeService;

    @Override
    public String getUserFlag(String code) {
        String code2SessionUrl = "https://api.weixin.qq.com/sns/jscode2session";
        Map<String, Object> param = new HashMap<String, Object>() {{
            this.put("appid", TokenUtil.APP_ID);
            this.put("secret", TokenUtil.APP_SECRET);
            this.put("js_code", code);
            this.put("grant_type", "authorization_code");
        }};
        return HttpUtil.doGet(code2SessionUrl, param);
    }

    @Override
    public void improveUserinfo(UserinfoDTO userinfoDTO, Long userId) {
        userinfoDTO.validate();
        User user = BeanUtil.copyProperties(userinfoDTO, User.class);
        user.setId(userId);
        // 修改
        this.lambdaUpdate().eq(User::getId, userId).update(user);
    }

    @Override
    public List<String> getPermissions(Long userId) {
        return Collections.emptyList();
    }

    @Override
    public User getUserByEmail(String email) {
        String redisKey = EMAIL_USER_MAP + email;
        return (User) redisCache.getCacheObject(redisKey).orElseGet(() -> {
            User user = this.lambdaQuery().eq(User::getEmail, email).one();
            redisCache.setCacheObject(redisKey, user, EMAIL_USER_TTL, EMAIL_USER_UNIT);
            return user;
        });
    }

    @Override
    public User getUserByOpenid(String openid) {
        String redisKey = WX_USER_MAP + openid;
        return (User) redisCache.getCacheObject(redisKey).orElseGet(() -> {
            User user = this.lambdaQuery().eq(User::getOpenid, openid).one();
            redisCache.setCacheObject(redisKey, user, WX_USER_TTL, WX_USER_UNIT);
            return user;
        });
    }

    @Override
    public String getOpenidByUserId(Long userId) {
        String redisKey = USERID_OPENID_MAP + userId;
        return (String) redisCache.getCacheObject(redisKey).orElseGet(() -> {
            User user = this.lambdaQuery().eq(User::getId, userId).one();
            String openid = Objects.isNull(user) ? null : user.getOpenid();
            redisCache.setCacheObject(redisKey, openid, USERID_OPENID_TTL, USERID_OPENID_UNIT);
            return openid;
        });
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
    public void deleteUserIdOpenIdCache(Long userId) {
        redisCache.deleteObject(USERID_OPENID_MAP + userId);
    }

    @Override
    public void bindingEmail(Long userId, String email, String code, String recordEmail) {
        // 检查验证码
        emailServiceFactory
                .getService(EmailServiceFactory.EMAIL_BINDING)
                .checkIdentifyingCode(email, code);
        // 判断邮箱用户是否存在
        User userByEmail = getUserByEmail(email);
        if (Objects.nonNull(userByEmail)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.EMAIL_USER_BE_BOUND);
        }
        this.lambdaUpdate()
                .eq(User::getId, userId)
                .set(User::getEmail, email)
                .update();
        deleteUserEmailCache(email);
        if (StringUtils.hasText(recordEmail)) {
            deleteUserEmailCache(recordEmail);
        }
        log.info("用户 {} 成功绑定 邮箱 {}", userId, email);
    }

    @Override
    public void bindingWx(Long userId, String randomCode, String code) {
        // 验证以下验证码
        wxBindingQRCodeService.checkParams(userId, randomCode);
        String resultJson = getUserFlag(code);
        Map<String, Object> response = JsonUtil.analyzeJson(resultJson, Map.class);
        String openid = (String) response.get("openid");
        String unionid = (String) response.get("unionid");
        if(Objects.isNull(openid)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.WX_CODE_NOT_VALID);
        }
        // 查询 openid 是否被注册过
        User userByOpenid = getUserByOpenid(openid);
        if (Objects.nonNull(userByOpenid)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.WX_USER_BE_BOUND);
        }
        // 判断当前用户是否绑定了微信
        // todo: 避免混乱所以现在暂且不支持微信重新绑定，之后需要再说
        String openidByUserId = getOpenidByUserId(userId);
        if (Objects.nonNull(openidByUserId)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_BOUND_WX);
        }
        this.lambdaUpdate()
                .eq(User::getId, userId)
                .set(User::getOpenid, openid)
                .update();
        deleteUserIdOpenIdCache(userId);
        deleteUserOpenidCache(openid);
        log.info("用户 {} 成功绑定 微信 {}", userId, openid);
    }

    private String uploadPhoto(byte[] photoData, Long userId, String originPhoto) {
        // 删除原头像（哪怕是字符串是网络路径/非法，只要本地没有完全对应上，就不算存在本地）
        String originSavePath = MediaUtil.getLocalFilePath(originPhoto);
        IOThreadPool.submit(() -> {
            MediaUtil.deleteFile(originSavePath);
        });
        // 下载头像到本地
        String mapPath = MediaUtil.saveImage(photoData, StaticMapperConfig.PHOTO_PATH);
        // 修改数据库
        this.lambdaUpdate()
                .set(User::getPhoto, mapPath)
                .eq(User::getId, userId)
                .update();
        return mapPath;
    }

    @Override
    public String tryUploadPhoto(byte[] photoData, Long userId, String originPhoto) {
        // 检查是否是图片
        if (!MediaUtil.isImage(photoData)) {
            throw new GlobalServiceException(String.format("用户 %d 上传非法文件", userId), GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
        String lock = USER_PHOTO_LOCK + userId;
        return redisLock.tryLockGetSomething(lock, 0L, timeout, TimeUnit.SECONDS, () -> uploadPhoto(photoData, userId, originPhoto), () -> {
            throw new GlobalServiceException(GlobalServiceStatusCode.REDIS_LOCK_FAIL);
        });
    }

    @Override
    public void onLoginState(String secret, String openid, String unionid) {
        String redisKey = QRCodeConfig.WX_LOGIN_QR_CODE_MAP + secret;
        Object check = redisCache.getCacheObject(redisKey).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.USER_LOGIN_CODE_VALID));
        if (Boolean.FALSE.equals(check)) {
            Map<String, Object> tokenData = new HashMap<String, Object>(){{
                this.put(ExtractUtil.OPENID, openid);
                this.put(ExtractUtil.UNIONID, unionid);
//                this.put(ExtractUtil.SESSION_KEY, sessionKey);
            }};
            String jsonData = JsonUtil.analyzeData(tokenData);
            String token = JwtUtil.createJWT(jsonData);
            redisCache.setCacheObject(redisKey, token,
                    QRCodeConfig.WX_LOGIN_QR_CODE_TTL, QRCodeConfig.WX_LOGIN_QR_CODE_UNIT);
        }
    }

    @Override
    public Map<String, Object> checkLoginState(String secret) {
        String redisKey = QRCodeConfig.WX_LOGIN_QR_CODE_MAP + secret;
        Object check = redisCache.getCacheObject(redisKey).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.USER_LOGIN_CODE_VALID));
        if (Boolean.FALSE.equals(check)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_LOGIN_NOT_CHECK);
        }
        redisCache.deleteObject(redisKey);
        return new HashMap<String, Object>() {{
            this.put(JwtUtil.JWT_HEADER, check);
        }};
    }
}