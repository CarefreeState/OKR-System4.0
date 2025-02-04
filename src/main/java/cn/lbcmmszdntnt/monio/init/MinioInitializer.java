package cn.lbcmmszdntnt.monio.init;

import cn.lbcmmszdntnt.common.exception.GlobalServiceException;
import cn.lbcmmszdntnt.monio.config.MinioConfig;
import cn.lbcmmszdntnt.monio.engine.MinioBucketEngine;
import cn.lbcmmszdntnt.monio.enums.MinioPolicyTemplate;
import cn.lbcmmszdntnt.monio.template.DefaultPolicyTemplate;
import cn.lbcmmszdntnt.template.engine.TextEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 1:52
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MinioInitializer implements ApplicationListener<ApplicationStartedEvent> {

    private final TextEngine textEngine;

    private final MinioConfig minioConfig;

    private final MinioBucketEngine minioBucketEngine;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        // 如果不存在，则初始化桶
        String bucketName = minioConfig.getBucketName();
        // 设置规则：所有人都能读（否则就只能获取）
        DefaultPolicyTemplate policyTemplate = DefaultPolicyTemplate.builder()
                .bucketName(bucketName)
                .build();
        String policy = textEngine.builder()
                .append(MinioPolicyTemplate.ALLOW_ALL_GET.getTemplate(), policyTemplate)
                .build();
        try {
            minioBucketEngine.tryMakeBucket(bucketName, policy);
        } catch (Exception e) {
            throw new GlobalServiceException(String.format("minio 桶 %s 创建失败", bucketName));
        }
    }
}
