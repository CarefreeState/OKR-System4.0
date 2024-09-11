package cn.lbcmmszdntnt.domain.medal.service;


import cn.lbcmmszdntnt.domain.medal.model.po.UserMedal;
import cn.lbcmmszdntnt.domain.medal.model.vo.UserMedalVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 马拉圈
* @description 针对表【user_medal(用户勋章关联表)】的数据库操作Service
* @createDate 2024-04-07 11:36:52
*/
public interface UserMedalService extends IService<UserMedal> {

    UserMedal getUserMedal(Long userId, Long medalId);

    void deleteDbUserMedalCache(Long userId, Long medalId);

    List<UserMedalVO> getUserMedalListAll(Long userId);

    List<UserMedalVO> getUserMedalListUnread(Long userId);

    void readUserMedal(Long userId, Long medalId);

}
