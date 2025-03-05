package cn.bitterfree.api.xxljob.model.entity;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XxlJobGroup implements Serializable {

    private Integer id;
    private String appname;
    private String title;
    private Integer addressType;        // 执行器地址类型：0=自动注册、1=手动录入
    private String addressList;     // 执行器地址列表，多地址逗号分隔(手动录入)

    // registry list
    private List<String> registryList;  // 执行器地址列表(系统注册)

    private static final long serialVersionUID = 1L;
}
