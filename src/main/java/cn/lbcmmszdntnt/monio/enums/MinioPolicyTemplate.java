package cn.lbcmmszdntnt.monio.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-25
 * Time: 14:08
 */
@Getter
@AllArgsConstructor
public enum MinioPolicyTemplate {

    ALLOW_ALL_GET("minio-allow-all-get.json", "允许全部读"),

    ;

    private final String template;

    private final String description;

}
