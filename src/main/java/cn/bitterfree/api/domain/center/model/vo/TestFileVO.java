package cn.bitterfree.api.domain.center.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-03-19
 * Time: 13:13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "测试相关文件信息")
public class TestFileVO {

    @Schema(description = "是否为文件夹")
    private Boolean isDir;

    @Schema(description = "文件夹/文件名")
    private String name;

}
