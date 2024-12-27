package cn.lbcmmszdntnt.domain.user.constants;

import cn.hutool.core.util.RandomUtil;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-12-25
 * Time: 21:59
 */
public interface UserPhotoConstants {

    String DEFAULT_PHOTO = "media/static/default.png";
    String DEFAULT_PHOTO1 = "media/static/default1.png";
    String DEFAULT_PHOTO2 = "media/static/default2.png";
    String DEFAULT_PHOTO3 = "media/static/default3.png";

    List<String> DEFAULT_PHOTO_LIST = List.of(DEFAULT_PHOTO, DEFAULT_PHOTO1, DEFAULT_PHOTO2, DEFAULT_PHOTO3);

    static String getDefaultPhoto() {
        return DEFAULT_PHOTO_LIST.get(RandomUtil.randomInt(DEFAULT_PHOTO_LIST.size()));
    }

}
