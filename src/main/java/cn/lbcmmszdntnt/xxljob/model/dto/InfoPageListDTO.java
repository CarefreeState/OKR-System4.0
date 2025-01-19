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
 * Time: 20:12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfoPageListDTO {

    private Integer jobGroup;

    private String executorHandler;

    private Integer triggerStatus;

    private String jobDesc;

    private String author;

}
