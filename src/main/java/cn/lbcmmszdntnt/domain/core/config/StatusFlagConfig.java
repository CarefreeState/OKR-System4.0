package cn.lbcmmszdntnt.domain.core.config;


import cn.lbcmmszdntnt.domain.core.config.properties.StatusFlagProperty;
import cn.lbcmmszdntnt.domain.core.model.entity.inner.StatusFlag;
import cn.lbcmmszdntnt.domain.core.model.mapper.inner.StatusFlagMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 13:49
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "status-flag")
public class StatusFlagConfig {

    private final static Map<String, Long> COLOR_CREDIT_MAP = new HashMap<>();

    private List<StatusFlagProperty> properties;

    private Double threshold;

    @Resource
    private StatusFlagMapper statusFlagMapper;

    @PostConstruct
    public void init() {
        properties.stream().parallel().forEach(statusFlagProperty -> {
            COLOR_CREDIT_MAP.put(statusFlagProperty.getColor(), statusFlagProperty.getCredit());
        });
    }

    public Long getCredit(String color) {
        Long credit = COLOR_CREDIT_MAP.get(color);
        return Objects.isNull(credit) ? 0L : credit;
    }

    public boolean isTouch(double average) {
        return average >= threshold;
    }

    public double calculateStatusFlag(List<StatusFlag> statusFlags) {
        if(Objects.isNull(statusFlags)) {
            return 0d;
        }
        int size = statusFlags.size();
        long sum = statusFlags
                .stream()
                .parallel()
                .map(statusFlag -> getCredit(statusFlag.getColor()))
                .reduce(Long::sum)
                .orElse(0L);
        return size == 0 ? 0 : (sum * 1.0) / size;
    }

    public double calculateStatusFlag(Long userId) {
        List<StatusFlag> statusFlags = statusFlagMapper.getStatusFlagsByUserId(userId);
        return calculateStatusFlag(statusFlags);
    }

    public double calculateCoreStatusFlag(Long quadrantId) {
        List<StatusFlag> statusFlags = statusFlagMapper.getStatusFlagsByQuadrantId(quadrantId);
        return calculateStatusFlag(statusFlags);
    }

}
