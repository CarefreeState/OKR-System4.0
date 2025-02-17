package cn.bitterfree.api.domain.medal.constants;

import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-13
 * Time: 3:15
 */
public interface MedalConstants {

    String USER_MEDAL_MAP_CACHE = "userMedalMapCache:";
    Long USER_MEDAL_MAP_TIMEOUT = 2L;
    TimeUnit USER_MEDAL_MAP_TIMEUNIT = TimeUnit.DAYS;

    String MEDAL_MAP_CACHE = "medalMapCache";
    Long MEDAL_MAP_TIMEOUT = 30L;
    TimeUnit MEDAL_MAP_TIMEUNIT = TimeUnit.DAYS;

    int KEY_RESULT_FULL_VALUE = 100;
    int COMMON_DEGREE_THRESHOLD = 80;
    int EXCELLENT_DEGREE_THRESHOLD = 100;

}
