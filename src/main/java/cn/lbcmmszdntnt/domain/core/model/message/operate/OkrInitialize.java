package cn.lbcmmszdntnt.domain.core.model.message.operate;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 11:55
 */
@Getter
@ToString
@Builder
public class OkrInitialize {

    private Long userId;

    private Long coreId;

}
