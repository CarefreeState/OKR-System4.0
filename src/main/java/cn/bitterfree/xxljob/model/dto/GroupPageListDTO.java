package cn.bitterfree.xxljob.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 19:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupPageListDTO {

    private String appname;

    private String title;
}
