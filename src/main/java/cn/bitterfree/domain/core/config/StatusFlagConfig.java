package cn.bitterfree.domain.core.config;


import cn.bitterfree.common.util.convert.ObjectUtil;
import cn.bitterfree.domain.core.model.entity.inner.StatusFlag;
import cn.bitterfree.domain.core.model.mapper.inner.StatusFlagMapper;
import cn.bitterfree.domain.core.service.OkrOperateService;
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
import java.util.stream.Collectors;

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

    private List<StatusFlagProperties> properties;

    private Double threshold;

    @Resource
    private StatusFlagMapper statusFlagMapper;

    @Resource
    private List<OkrOperateService> okrOperateServiceList;

    @PostConstruct
    public void init() {
        properties.stream().parallel().forEach(statusFlagProperties -> {
            COLOR_CREDIT_MAP.put(statusFlagProperties.getColor(), statusFlagProperties.getCredit());
        });
    }

    public Long getCredit(String color) {
        Long credit = COLOR_CREDIT_MAP.get(color);
        return Objects.isNull(credit) ? 0L : credit;
    }

    public boolean isTouch(double average) {
        return average >= threshold;
    }

    public double calculateStatusFlagList(List<StatusFlag> statusFlags) {
        if(Objects.isNull(statusFlags)) {
            return 0d;
        }
        int size = statusFlags.size();
        long sum = statusFlags.stream()
                .map(statusFlag -> getCredit(statusFlag.getColor()))
                .reduce(Long::sum)
                .orElse(0L);
        return size == 0 ? 0 : (sum * 1.0) / size;
    }

    public Map<Long, Double> calculateStatusFlag(List<Long> ids) {
        Map<Long, List<StatusFlag>> resultMap = new HashMap<>();
        ObjectUtil.nonNullstream(okrOperateServiceList)
                .map(service -> service.getStatusFlagsByUserId(ids))
                .flatMap(List::stream)
                .forEach(vo -> {
                    Long userId = vo.getUserId();
                    if(resultMap.containsKey(userId)) {
                        resultMap.get(userId).addAll(vo.getStatusFlags());
                    } else {
                        resultMap.put(userId, vo.getStatusFlags());
                    }
                });
        return resultMap.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> calculateStatusFlagList(entry.getValue()),
                (oldData, newData) -> newData
        ));
    }

    public double calculateCoreStatusFlag(Long quadrantId) {
        return calculateStatusFlagList(statusFlagMapper.getStatusFlagsByQuadrantId(quadrantId));
    }

}
