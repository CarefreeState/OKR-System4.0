package cn.lbcmmszdntnt.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-12-24
 * Time: 10:02
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenVO {

    private Long userId;

}
