package cn.lbcmmszdntnt.domain.user.constants;

import cn.hutool.core.util.RandomUtil;
import cn.lbcmmszdntnt.domain.user.enums.UserType;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-07
 * Time: 15:58
 */
public interface UserConstants {

    UserType DEFAULT_USER_TYPE = UserType.NORMAL_USER; // 如果默认是封禁，也就是说每个用户都得经过审核（实际系统并非如此）

    String DEFAULT_PHOTO = "media/static/default.png";
    String DEFAULT_PHOTO1 = "media/static/default1.png";
    String DEFAULT_PHOTO2 = "media/static/default2.png";
    String DEFAULT_PHOTO3 = "media/static/default3.png";
    String USER_PHOTO_LOCK = "userPhotoLock:";

    List<String> DEFAULT_PHOTO_LIST = List.of(DEFAULT_PHOTO, DEFAULT_PHOTO1, DEFAULT_PHOTO2, DEFAULT_PHOTO3);

    static String getDefaultPhoto() {
        return DEFAULT_PHOTO_LIST.get(RandomUtil.randomInt(DEFAULT_PHOTO_LIST.size()));
    }

}
