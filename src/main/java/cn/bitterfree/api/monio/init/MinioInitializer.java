package cn.bitterfree.api.monio.init;

import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.common.util.convert.JsonUtil;
import cn.bitterfree.api.monio.config.MinioConfig;
import cn.bitterfree.api.monio.engine.MinioBucketEngine;
import cn.bitterfree.api.monio.enums.MinioPolicyTemplate;
import cn.bitterfree.api.monio.template.DefaultPolicyTemplate;
import cn.bitterfree.api.template.engine.TextEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Map;

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
        String bucketName = minioConfig.getBucketName();
        try {
            // 如果不存在，则初始化桶
            minioBucketEngine.tryMakeBucket(bucketName);
            // 设置规则：所有人都能读（否则就只能获取）
            DefaultPolicyTemplate policyTemplate = DefaultPolicyTemplate.builder()
                    .bucketName(bucketName)
                    .build();
            String policy = textEngine.builder()
                    .append(MinioPolicyTemplate.ALLOW_ALL_GET.getTemplate(), policyTemplate)
                    .build();
            log.info("尝试初始化 minio 桶 {}，设置策略 {}", bucketName, JsonUtil.parse(policy, Map.class));
            minioBucketEngine.setBucketPolicy(bucketName, policy);
        } catch (Exception e) {
            throw new GlobalServiceException(String.format("minio 桶 %s 创建失败", bucketName));
        }
    }
}
