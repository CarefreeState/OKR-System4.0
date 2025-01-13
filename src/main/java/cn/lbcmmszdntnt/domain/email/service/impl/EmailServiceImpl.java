package cn.lbcmmszdntnt.domain.email.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.email.model.vo.VerificationCodeTemplate;
import cn.lbcmmszdntnt.domain.email.service.EmailService;
import cn.lbcmmszdntnt.domain.email.util.IdentifyingCodeValidator;
import cn.lbcmmszdntnt.email.enums.EmailTemplateEnum;
import cn.lbcmmszdntnt.email.model.po.EmailMessage;
import cn.lbcmmszdntnt.email.sender.EmailSender;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import cn.lbcmmszdntnt.redis.cache.RedisMapCache;
import cn.lbcmmszdntnt.template.engine.HtmlEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static cn.lbcmmszdntnt.domain.email.util.IdentifyingCodeValidator.*;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-18
 * Time: 11:49
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final int IDENTIFYING_CODE_MINUTES = 5;//过期分钟数

    private static final TimeUnit IDENTIFYING_CODE_UNIT = TimeUnit.MINUTES;//过期分钟

    private static final int IDENTIFYING_CODE_CD_MINUTES = 1;//CD分钟数

    private static final TimeUnit IDENTIFYING_CODE_CD_UNIT = TimeUnit.MINUTES;//CD分钟

    private static final long IDENTIFYING_CODE_TIMEOUT = IDENTIFYING_CODE_UNIT.toMillis(IDENTIFYING_CODE_MINUTES); //单位为毫秒

    private static final long IDENTIFYING_CODE_INTERVAL_Limit = IDENTIFYING_CODE_CD_UNIT.toMillis(IDENTIFYING_CODE_CD_MINUTES); // 两次发送验证码的最短时间间隔

    private static final int IDENTIFYING_CODE_INTERVAL_LIMIT = 5; // 只有五次验证机会

    private final EmailSender emailSender;

    private final HtmlEngine htmlEngine;

    private final RedisMapCache redisMapCache;

    private final RedisCache redisCache;

    private boolean canSendEmail(long ttl) {
        return ttl > IDENTIFYING_CODE_TIMEOUT - IDENTIFYING_CODE_INTERVAL_Limit;
    }

    private long getCanSendSeconds(long ttl) {
        return TimeUnit.MILLISECONDS.toSeconds(ttl + IDENTIFYING_CODE_INTERVAL_Limit - IDENTIFYING_CODE_TIMEOUT);
    }

    private String getRedisKey(String type, String email) {
        return String.format(REDIS_EMAIL_CODE, type, email);
    }

    @Override
    public void sendIdentifyingCode(String type, String email, String code) {
        final String redisKey = getRedisKey(type, email);
        // 验证一下一分钟以内发过了没有
        long ttl = redisCache.getKeyTTL(redisKey, TimeUnit.MILLISECONDS); // 小于 0 则代表没有到期时间或者不存在，允许发送
        if(Boolean.TRUE.equals(canSendEmail(ttl))) {
            String message = String.format("请在 %d 秒后再重新申请", getCanSendSeconds(ttl));
            throw new GlobalServiceException(message, GlobalServiceStatusCode.EMAIL_SEND_FAIL);
        }
        // 封装 Email
        EmailTemplateEnum captcha = EmailTemplateEnum.CAPTCHA;
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setTitle(captcha.getTitle());
        emailMessage.setRecipient(email);
        // 存到 redis 中
        Map<String, Object> data = new HashMap<>();
        data.put(IdentifyingCodeValidator.IDENTIFYING_CODE, code); // 验证码
        data.put(IdentifyingCodeValidator.IDENTIFYING_OPPORTUNITIES, IDENTIFYING_CODE_INTERVAL_LIMIT); // 有效次数
        redisMapCache.init(redisKey, data, IDENTIFYING_CODE_TIMEOUT, TimeUnit.MILLISECONDS);
        VerificationCodeTemplate verificationCodeTemplate = VerificationCodeTemplate.builder()
                .code(code)
                .timeout((int) TimeUnit.MILLISECONDS.toMinutes(IDENTIFYING_CODE_TIMEOUT))
                .build();
        // 发送模板消息
        String html = htmlEngine.builder()
                .append(captcha.getTemplate(), verificationCodeTemplate)
                .build();
        emailMessage.setContent(html);
        emailSender.send(emailMessage);
        log.info("发送验证码：{} -> email:{}", code, email);
    }

    @Override
    public void checkIdentifyingCode(String type, String email, String code) {
        final String redisKey = getRedisKey(type, email);
        if(!redisCache.isExists(redisKey)) {
            String message = String.format("不存在邮箱[%s]的相关记录", email);
            throw new GlobalServiceException(message, GlobalServiceStatusCode.EMAIL_NOT_EXIST_RECORD);
        }
        // 取出验证码和过期时间点
        String codeValue = redisMapCache.get(redisKey, IDENTIFYING_CODE, String.class).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.EMAIL_CODE_OPPORTUNITIES_EXHAUST));
        Long opportunities = redisMapCache.get(redisKey, IDENTIFYING_OPPORTUNITIES, Long.class).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.EMAIL_CODE_NOT_CONSISTENT));
        // 还有没有验证机会
        if (opportunities.compareTo(1L) < 0) {
            throw new GlobalServiceException(GlobalServiceStatusCode.EMAIL_CODE_OPPORTUNITIES_EXHAUST);
        }
        // 验证是否正确
        if (!codeValue.equals(code)) {
            // 次数减一
            opportunities = redisMapCache.increment(redisKey, IdentifyingCodeValidator.IDENTIFYING_OPPORTUNITIES, -1);
            String message = String.format("验证码错误，剩余%d次机会", opportunities);
            throw new GlobalServiceException(message, GlobalServiceStatusCode.EMAIL_CODE_NOT_CONSISTENT);
        }
        // 验证成功
        redisCache.deleteObject(redisKey);
    }
}
