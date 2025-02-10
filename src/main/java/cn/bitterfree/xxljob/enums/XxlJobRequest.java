package cn.bitterfree.xxljob.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 18:55
 */
@Getter
@AllArgsConstructor
public enum XxlJobRequest {

    LOGIN("/login", "POST"),
    GROUP_PAGE_LIST("/jobgroup/pageList", "POST"),
    INFO_PAGE_LIST("/jobinfo/pageList", "POST"),
    GROUP_SAVE("/jobgroup/save", "POST"),
    GROUP_UPDATE("/jobgroup/update", "POST"),
    INFO_ADD("/jobinfo/add", "POST"),
    INFO_UPDATE("/jobinfo/update", "POST");

    private final String uri;

    private final String method;

}
