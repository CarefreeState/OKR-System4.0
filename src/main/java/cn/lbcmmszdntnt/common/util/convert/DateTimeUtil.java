package cn.lbcmmszdntnt.common.util.convert;

import cn.hutool.core.date.DateUtil;
import cn.lbcmmszdntnt.common.constants.DateTimeConstants;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-12
 * Time: 19:45
 */
public class DateTimeUtil {

    public static String getDateFormat(Date date) {
        return new SimpleDateFormat(DateTimeConstants.DATE_TIME_PATTERN).format(date);
    }

    public static String getOnlyDateFormat(Date date) {
        return new SimpleDateFormat(DateTimeConstants.DATE_PATTERN).format(date);
    }

    public static Date beginOfDay(Date date) {
        return DateUtil.beginOfDay(date);
    }

}
