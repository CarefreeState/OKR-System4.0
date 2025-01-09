package cn.lbcmmszdntnt.domain.auth.service;

import cn.lbcmmszdntnt.domain.auth.model.dto.LoginDTO;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 12:48
 */
public interface LoginService {

    @Transactional
    User login(LoginDTO loginDTO);

}
