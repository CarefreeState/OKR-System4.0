package cn.lbcmmszdntnt.interceptor.config.properties;

import cn.lbcmmszdntnt.domain.user.enums.UserType;
import lombok.*;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-09
 * Time: 14:37
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class InterceptProperties {

    private List<UserType> permit;

    private Boolean authenticate;

    private Boolean authorize;

}
