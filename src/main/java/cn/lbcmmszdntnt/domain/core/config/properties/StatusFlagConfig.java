package cn.lbcmmszdntnt.domain.core.config.properties;


import cn.hutool.extra.spring.SpringUtil;
import cn.lbcmmszdntnt.domain.core.model.mapper.inner.StatusFlagMapper;
import cn.lbcmmszdntnt.domain.core.model.po.inner.StatusFlag;
import jakarta.annotation.PostConstruct;
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
@Setter
@Configuration
@ConfigurationProperties(prefix = "medal.status-flag")
public class StatusFlagConfig {

    private List<StatusFlagProperty> statusFlagProperties;

    private Double threshold;

    private final Map<String, Long> colorCreditMap = new HashMap<>();

    private final StatusFlagMapper statusFlagMapper = SpringUtil.getBean(StatusFlagMapper.class);

    @PostConstruct
    public void doPostConstruct() {
        statusFlagProperties.stream().parallel().forEach(statusFlagProperty -> {
            colorCreditMap.put(statusFlagProperty.getColor(), statusFlagProperty.getCredit());
        });
    }

    public Long getCredit(String color) {
        Long credit = colorCreditMap.get(color);
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
