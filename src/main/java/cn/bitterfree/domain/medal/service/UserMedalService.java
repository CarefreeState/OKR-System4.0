package cn.bitterfree.domain.medal.service;


import cn.bitterfree.domain.medal.model.entity.UserMedal;
import cn.bitterfree.domain.medal.model.vo.UserMedalVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
* @author 马拉圈
* @description 针对表【user_medal(用户勋章关联表)】的数据库操作Service
* @createDate 2024-04-07 11:36:52
*/
public interface UserMedalService extends IService<UserMedal> {

    Map<Long, UserMedal> getUserMedalMap(Long userId);

    void saveUserMedal(Long userId, Long medalId, UserMedal dbUserMedal, Long newCredit, Integer coefficient);

    UserMedal getUserMedal(Long userId, Long medalId);

    List<UserMedalVO> getUserMedalListAll(Long userId);

    List<UserMedalVO> getUserMedalListUnread(Long userId);

    void readUserMedal(Long userId, Long medalId);

}
