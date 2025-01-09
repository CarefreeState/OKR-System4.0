package cn.lbcmmszdntnt.domain.core.context;

import cn.lbcmmszdntnt.common.util.thread.local.ThreadLocalMapUtil;
import cn.lbcmmszdntnt.domain.user.model.entity.User;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-07
 * Time: 15:00
 */
public class OkrCoreContext {

    private final static String CORE_ID = "coreId";

    public static Long getCoreId() {
        return ThreadLocalMapUtil.get(CORE_ID, Long.class);
    }

    public static void setCoreId(Long coreId) {
        ThreadLocalMapUtil.set(CORE_ID, coreId);
    }


}
