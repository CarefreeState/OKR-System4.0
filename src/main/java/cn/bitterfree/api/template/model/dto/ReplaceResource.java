package cn.bitterfree.api.template.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-09
 * Time: 11:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplaceResource {

    private String target;

    private String replacement;

}
