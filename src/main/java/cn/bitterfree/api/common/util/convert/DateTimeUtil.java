package cn.bitterfree.api.common.util.convert;

import cn.bitterfree.api.common.constants.DateTimeConstants;
import cn.hutool.core.date.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-12
 * Time: 19:45
 */
public class DateTimeUtil {

    public static String getDateFormat(Date date) {
        return Optional.ofNullable(date).map(d -> new SimpleDateFormat(DateTimeConstants.DATE_TIME_PATTERN).format(d)).orElse(null);
    }

    public static String getOnlyDateFormat(Date date) {
        return Optional.ofNullable(date).map(d -> new SimpleDateFormat(DateTimeConstants.DATE_PATTERN).format(d)).orElse(null);
    }

    public static Date beginOfDay(Date date) {
        return Optional.ofNullable(date).map(DateUtil::beginOfDay).orElse(null);
    }

}
