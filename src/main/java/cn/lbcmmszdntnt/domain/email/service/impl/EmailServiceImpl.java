package cn.lbcmmszdntnt.domain.email.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.email.model.vo.VerificationCodeTemplate;
import cn.lbcmmszdntnt.domain.email.repository.EmailRepository;
import cn.lbcmmszdntnt.domain.email.service.EmailService;
import cn.lbcmmszdntnt.email.model.po.EmailMessage;
import cn.lbcmmszdntnt.email.sender.EmailSender;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.template.engine.HtmlEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    @Value("${email.template}")
    private String emailModelHtml; // Email 验证码通知 -模板

    @Value("${spring.mail.username}")
    private String systemEmail;

    private final EmailSender emailSender;

    private final EmailRepository emailRepository;

    private final HtmlEngine htmlEngine;

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
        long ttl = emailRepository.getTTLOfCode(redisKey); // 小于 0 则代表没有到期时间或者不存在，允许发送
        if(Boolean.TRUE.equals(canSendEmail(ttl))) {
            String message = String.format("请在 %d 秒后再重新申请", getCanSendSeconds(ttl));
            throw new GlobalServiceException(message, GlobalServiceStatusCode.EMAIL_SEND_FAIL);
        }
        // 封装 Email
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setContent(code);
        emailMessage.setCreateTime(new Date());
        emailMessage.setTitle(IDENTIFYING_CODE_PURPOSE);
        emailMessage.setRecipient(email);
        emailMessage.setCarbonCopy();
        emailMessage.setSender(systemEmail);
        // 存到 redis 中
        emailRepository.setIdentifyingCode(redisKey, code, IDENTIFYING_CODE_TIMEOUT, IDENTIFYING_CODE_INTERVAL_LIMIT);
        VerificationCodeTemplate verificationCodeTemplate = VerificationCodeTemplate.builder()
                .code(code)
                .timeout((int) TimeUnit.MILLISECONDS.toMinutes(IDENTIFYING_CODE_TIMEOUT))
                .build();
        // 发送模板消息
        String html = htmlEngine.builder()
                .append(emailModelHtml, verificationCodeTemplate)
                .build();
        emailMessage.setContent(html);
        emailSender.send(emailMessage);
        log.info("发送验证码:{} -> email:{}", code, email);
    }

    @Override
    public void checkIdentifyingCode(String type, String email, String code) {
        final String redisKey = getRedisKey(type, email);
        Map<String, Object> map = emailRepository.getIdentifyingCode(redisKey)
                .map(value -> (Map<String, Object>)value)
                .orElseThrow(() -> {
                    String message = String.format("不存在邮箱[%s]的相关记录", email);
                    return new GlobalServiceException(message, GlobalServiceStatusCode.EMAIL_NOT_EXIST_RECORD);
                });
        // 取出验证码和过期时间点
        String codeValue = (String) map.get(IDENTIFYING_CODE);
        int opportunities = (int) map.get(IDENTIFYING_OPPORTUNITIES);
        // 还有没有验证机会
        if (opportunities < 1) {
            throw new GlobalServiceException(GlobalServiceStatusCode.EMAIL_CODE_OPPORTUNITIES_EXHAUST);
        }
        // 验证是否正确
        if (!codeValue.equals(code)) {
            // 次数减一
            opportunities = (int)emailRepository.decrementOpportunities(redisKey);
            String message = String.format("验证码错误，剩余%d次机会", opportunities);
            throw new GlobalServiceException(message, GlobalServiceStatusCode.EMAIL_CODE_NOT_CONSISTENT);
        }
        // 验证成功
        emailRepository.deleteIdentifyingCodeRecord(redisKey);
    }
}
