package cn.bitterfree.api.domain.auth.service.impl;

import cn.bitterfree.api.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.domain.auth.constants.AuthConstants;
import cn.bitterfree.api.domain.auth.enums.EmailIdentifyType;
import cn.bitterfree.api.domain.auth.model.vo.EmailIdentifyTemplateVO;
import cn.bitterfree.api.domain.auth.service.EmailIdentifyService;
import cn.bitterfree.api.domain.auth.service.ValidateService;
import cn.bitterfree.api.email.model.dto.EmailMessage;
import cn.bitterfree.api.email.sender.EmailSender;
import cn.bitterfree.api.redis.cache.RedisCache;
import cn.bitterfree.api.redis.cache.RedisMapCache;
import cn.bitterfree.api.template.engine.HtmlEngine;
import cn.hutool.core.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static cn.bitterfree.api.common.enums.EmailTemplate.EMAIL_IDENTIFY;


/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-11-12
 * Time: 16:47
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmailIdentifyServiceImpl implements EmailIdentifyService {

    private final HtmlEngine htmlEngine;

    private final EmailSender emailSender;

    private final RedisCache redisCache;

    private final RedisMapCache redisMapCache;

    private final ValidateService validateService;

    private void buildEmailAndSend(String email, String code) {
        // 封装 Email
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setTitle(EMAIL_IDENTIFY.getTitle());
        emailMessage.setRecipient(email);
        // 构造模板消息
        EmailIdentifyTemplateVO emailIdentifyTemplateVO = EmailIdentifyTemplateVO.builder()
                .code(code)
                .timeout(AuthConstants.EMAIL_IDENTIFY_TIMEUNIT.toMinutes(AuthConstants.EMAIL_IDENTIFY_TIMEOUT))
                .build();
        String html = htmlEngine.builder()
                .append(EMAIL_IDENTIFY.getTemplate(), emailIdentifyTemplateVO)
                .build();
        emailMessage.setContent(html);
        // 发送模板消息
        emailSender.send(emailMessage);
    }

    @Override
    public String sendIdentifyingCode(EmailIdentifyType emailIdentifyType, String email) {
        // 检查是否为风控用户, 如果是直接跳过
        if (redisCache.isExists(AuthConstants.EMAIL_BLOCKED_USER + email)) {
            String message = String.format("邮箱 %s 已被风控，%d 小时后解封", email, AuthConstants.EMAIL_BLOCKED_TIMEUNIT.toHours(AuthConstants.EMAIL_BLOCKED_TIMEOUT));
            // 申请验证码次数用尽
            throw new GlobalServiceException(message, GlobalServiceStatusCode.EMAIL_IDENTIFY_CODE_COUNT_EXHAUST);
        }

        // 拼接验证码在 redis 缓存的 map 的 key
        String identifyType = emailIdentifyType.getName();
        String redisKey = String.format(AuthConstants.EMAIL_IDENTIFY_CODE_MAP, identifyType, email);
        // 验证一下一分钟以内发过了没有
        long ttl = redisCache.getKeyTTL(redisKey, TimeUnit.MILLISECONDS); // 小于 0 则代表没有到期时间或者不存在，允许发送
        if (ttl > TimeUnit.MINUTES.toMillis((AuthConstants.EMAIL_IDENTIFY_TIMEOUT - AuthConstants.EMAIL_IDENTIFY_RATE_LIMIT))) {
            String message = String.format("申请太频繁, 请在 %d 分钟后再重新申请", AuthConstants.EMAIL_IDENTIFY_RATE_LIMIT);
            throw new GlobalServiceException(message, GlobalServiceStatusCode.EMAIL_SEND_FAIL);
        }
        // 获取缓存
        String code = RandomUtil.randomNumbers(AuthConstants.EMAIL_IDENTIFY_CODE_LENGTH);
        redisMapCache.getMap(redisKey, String.class, Object.class).ifPresentOrElse(cache -> {
                    long curRetryCount = redisMapCache.increment(redisKey, AuthConstants.EMAIL_IDENTIFY_CODE_COUNT_KEY, -1);
                    if (curRetryCount < 0) {
                        // 申请验证码次数用尽
                        redisCache.setObject(AuthConstants.EMAIL_BLOCKED_USER + email, 0, AuthConstants.EMAIL_BLOCKED_TIMEOUT, AuthConstants.EMAIL_BLOCKED_TIMEUNIT);
                    }
                    redisMapCache.put(redisKey, AuthConstants.EMAIL_IDENTIFY_CODE_KEY, code);
                    redisCache.expire(redisKey, AuthConstants.EMAIL_IDENTIFY_TIMEOUT, AuthConstants.EMAIL_IDENTIFY_TIMEUNIT);
                }, () -> {
                    // 如果 redis 没有对应 key 值，初始化
                    Map<String, Object> captchaCodeMap = new HashMap<>();
                    captchaCodeMap.put(AuthConstants.EMAIL_IDENTIFY_CODE_COUNT_KEY, AuthConstants.EMAIL_IDENTIFY_CODE_MAX_RETRY_COUNT - 1);
                    captchaCodeMap.put(AuthConstants.EMAIL_IDENTIFY_CODE_KEY, code);
                    redisMapCache.init(redisKey, captchaCodeMap, AuthConstants.EMAIL_IDENTIFY_TIMEOUT, AuthConstants.EMAIL_IDENTIFY_TIMEUNIT);
                }
        );
        // 发送模板消息
        buildEmailAndSend(email, code);
        return code;
    }

    @Override
    public void validateEmailCode(EmailIdentifyType emailIdentifyType, String email, String code) {
        String identifyType = emailIdentifyType.getName();
        String redisKey = String.format(AuthConstants.EMAIL_IDENTIFY_CODE_MAP, identifyType, email);
        validateService.validate(String.format(AuthConstants.VALIDATE_EMAIL_CODE_KEY, identifyType, email), () -> {
            return redisMapCache.get(redisKey, AuthConstants.EMAIL_IDENTIFY_CODE_KEY, String.class).orElseThrow(() -> {
                String message = String.format("不存在邮箱 %s 的 %s 的相关记录", email, emailIdentifyType.getDescription());
                return new GlobalServiceException(message, GlobalServiceStatusCode.EMAIL_NOT_EXIST_RECORD);
            }).equals(code);
        }, emailIdentifyType.getErrorCode());
        redisCache.deleteObject(redisKey);
    }
}
