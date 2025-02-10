package cn.bitterfree.domain.core.model.message.operate;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-22
 * Time: 16:35
 */
@Getter
@ToString
@Builder
public class StatusFlagUpdate {

    private Long userId;

    private Long coreId;
}
