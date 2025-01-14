package cn.lbcmmszdntnt.xxljob.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 20:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupSaveDTO {

    private String appname;

    private String title;

    private String addressType;

    private String addressList;

}
