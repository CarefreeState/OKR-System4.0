package cn.lbcmmszdntnt.domain.user.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.config.WebMvcConfiguration;
import cn.lbcmmszdntnt.domain.email.service.EmailService;
import cn.lbcmmszdntnt.domain.email.util.IdentifyingCodeValidator;
import cn.lbcmmszdntnt.domain.qrcode.config.QRCodeConfig;
import cn.lbcmmszdntnt.domain.qrcode.service.WxBindingQRCodeService;
import cn.lbcmmszdntnt.domain.user.model.converter.UserConverter;
import cn.lbcmmszdntnt.domain.user.model.dto.UserinfoDTO;
import cn.lbcmmszdntnt.domain.user.model.mapper.UserMapper;
import cn.lbcmmszdntnt.domain.user.model.po.User;
import cn.lbcmmszdntnt.domain.user.service.UserService;
import cn.lbcmmszdntnt.domain.user.util.ExtractUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import cn.lbcmmszdntnt.redis.lock.RedisLock;
import cn.lbcmmszdntnt.redis.lock.RedisLockProperties;
import cn.lbcmmszdntnt.util.convert.JsonUtil;
import cn.lbcmmszdntnt.util.jwt.JwtUtil;
import cn.lbcmmszdntnt.util.media.FileResourceUtil;
import cn.lbcmmszdntnt.util.media.MediaUtil;
import cn.lbcmmszdntnt.util.thread.pool.IOThreadPool;
import cn.lbcmmszdntnt.util.web.HttpUtil;
import cn.lbcmmszdntnt.wxtoken.TokenUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    private final static String EMAIL_USER_MAP = "emailUserMap:";

    private final static String WX_USER_MAP = "wxUserMap:";

    private final static String ID_USER_MAP = "idUserMap:";

    private final static String USER_PHOTO_LOCK = "userPhotoLock:";

    private final static Long EMAIL_USER_TTL = 2L;

    private final static Long WX_USER_TTL = 2L;

    private final static Long ID_USER_TTL = 2L;

    private final static TimeUnit EMAIL_USER_UNIT = TimeUnit.HOURS;

    private final static TimeUnit WX_USER_UNIT = TimeUnit.HOURS;

    private final static TimeUnit ID_USER_UNIT = TimeUnit.HOURS;

    private final RedisCache redisCache;

    private final RedisLock redisLock;

    private final RedisLockProperties redisLockProperties;

    private final WxBindingQRCodeService wxBindingQRCodeService;

    private final EmailService emailService;

    @Override
    public String getUserFlag(String code) {
        String code2SessionUrl = "https://api.weixin.qq.com/sns/jscode2session";
        Map<String, Object> param = new HashMap<>() {{
            this.put("appid", TokenUtil.APP_ID);
            this.put("secret", TokenUtil.APP_SECRET);
            this.put("js_code", code);
            this.put("grant_type", "authorization_code");
        }};
        return HttpUtil.doGet(code2SessionUrl, param);
    }

    @Override
    public List<String> getPermissions(Long userId) {
        // todo: 权限获取的业务
        return Collections.emptyList();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        String redisKey = ID_USER_MAP + id;
        return Optional.ofNullable(redisCache.getObject(redisKey, User.class).orElseGet(() -> {
            User user = this.lambdaQuery().eq(User::getId, id).one();
            redisCache.setObject(redisKey, user, ID_USER_TTL, ID_USER_UNIT);
            return user;
        }));
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        String redisKey = EMAIL_USER_MAP + email;
        return Optional.ofNullable(redisCache.getObject(redisKey, User.class).orElseGet(() -> {
            User user = this.lambdaQuery().eq(User::getEmail, email).one();
            redisCache.setObject(redisKey, user, EMAIL_USER_TTL, EMAIL_USER_UNIT);
            return user;
        }));
    }

    @Override
    public Optional<User> getUserByOpenid(String openid) {
        String redisKey = WX_USER_MAP + openid;
        return Optional.ofNullable(redisCache.getObject(redisKey, User.class).orElseGet(() -> {
            User user = this.lambdaQuery().eq(User::getOpenid, openid).one();
            redisCache.setObject(redisKey, user, WX_USER_TTL, WX_USER_UNIT);
            return user;
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
        wxBindingQRCodeService.checkParams(userId, randomCode);
        String resultJson = getUserFlag(code);
        Map<String, Object> response = JsonUtil.analyzeJson(resultJson, Map.class);
        String openid = (String) response.get("openid");
        String unionid = (String) response.get("unionid");
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

    private String uploadPhoto(byte[] photoData, Long userId, String originPhoto) {
        // 删除原头像（哪怕是字符串是网络路径/非法，只要本地没有完全对应上，就不算存在本地）
        String originSavePath = MediaUtil.getLocalFilePath(originPhoto);
        IOThreadPool.submit(() -> {
            MediaUtil.deleteFile(originSavePath);
        });
        // 下载头像到本地
        String mapPath = MediaUtil.saveImage(photoData, WebMvcConfiguration.PHOTO_PATH);
        // 修改数据库
        this.lambdaUpdate()
                .set(User::getPhoto, mapPath)
                .eq(User::getId, userId)
                .update();
        deleteUserAllCache(userId);
        return mapPath;
    }

    @Override
    public String tryUploadPhoto(byte[] photoData, Long userId, String originPhoto) {
        // 检查是否是图片
        if (!FileResourceUtil.isImage(MediaUtil.getContentType(photoData))) {
            throw new GlobalServiceException(String.format("用户 %d 上传非法文件", userId), GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
        String lock = USER_PHOTO_LOCK + userId;
        return redisLock.tryLockGetSomething(lock, 0L, redisLockProperties.getTimeout(), TimeUnit.SECONDS,
                () -> uploadPhoto(photoData, userId, originPhoto),
                () -> {
                    throw new GlobalServiceException(GlobalServiceStatusCode.REDIS_LOCK_FAIL);
                });
    }

    @Override
    public void onLoginState(String secret, String openid, String unionid) {
        String redisKey = QRCodeConfig.WX_LOGIN_QR_CODE_MAP + secret;
        String token = redisCache.getObject(redisKey, String.class).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.USER_LOGIN_CODE_VALID));
        if ("null".equals(token)) {
            Map<String, Object> tokenData = new HashMap<String, Object>(){{
                this.put(ExtractUtil.OPENID, openid);
                this.put(ExtractUtil.UNIONID, unionid);
//                this.put(ExtractUtil.SESSION_KEY, sessionKey);
            }};
            String jsonData = JsonUtil.analyzeData(tokenData);
            redisCache.setObject(redisKey, JwtUtil.createJwt(jsonData),
                    QRCodeConfig.WX_LOGIN_QR_CODE_TTL, QRCodeConfig.WX_LOGIN_QR_CODE_UNIT);
        }
    }

    @Override
    public Map<String, Object> checkLoginState(String secret) {
        String redisKey = QRCodeConfig.WX_LOGIN_QR_CODE_MAP + secret;
        String token = redisCache.getObject(redisKey, String.class).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.USER_LOGIN_CODE_VALID));
        if ("null".equals(token)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_LOGIN_NOT_CHECK);
        }
        redisCache.deleteObject(redisKey);
        return new HashMap<String, Object>() {{
            this.put(JwtUtil.JWT_HEADER, token);
        }};
    }
}