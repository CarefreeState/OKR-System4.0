package cn.lbcmmszdntnt.domain.core.model.event;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-22
 * Time: 16:34
 */
@Getter
@ToString
@Builder
public class KeyResultUpdate {

    private Long userId;

    private Long coreId;

    private Integer probability;

    private Integer oldProbability;
}
