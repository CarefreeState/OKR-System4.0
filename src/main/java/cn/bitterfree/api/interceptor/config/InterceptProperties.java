package cn.bitterfree.api.interceptor.config;

import cn.bitterfree.api.domain.user.enums.UserType;
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
@ToString
public class InterceptProperties {

    private List<UserType> permit;

    private Boolean authenticate;

    private Boolean authorize;

}
