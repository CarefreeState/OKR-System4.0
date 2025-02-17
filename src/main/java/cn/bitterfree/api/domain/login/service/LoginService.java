package cn.bitterfree.api.domain.login.service;

import cn.bitterfree.api.domain.login.model.dto.LoginDTO;
import cn.bitterfree.api.domain.user.model.entity.User;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 12:48
 */
public interface LoginService {

    User login(LoginDTO loginDTO);

}
