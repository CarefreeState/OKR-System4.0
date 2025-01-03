package cn.lbcmmszdntnt.domain.user.service;

import cn.lbcmmszdntnt.domain.user.model.dto.LoginDTO;
import cn.lbcmmszdntnt.domain.user.model.po.User;
import jakarta.servlet.http.HttpServletRequest;
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

    void logout(HttpServletRequest request);

}
