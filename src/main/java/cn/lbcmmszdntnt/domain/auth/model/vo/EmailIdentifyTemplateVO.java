package cn.lbcmmszdntnt.domain.auth.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-11-12
 * Time: 17:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailIdentifyTemplateVO {

    private String code; // 验证码

    private Long timeout; // 过期时间分钟数

}
