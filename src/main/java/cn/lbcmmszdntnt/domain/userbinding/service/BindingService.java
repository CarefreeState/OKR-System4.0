package cn.lbcmmszdntnt.domain.userbinding.service;

import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.domain.userbinding.model.dto.BindingDTO;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 1:24
 */
public interface BindingService {

    void binding(User user, BindingDTO bindingDTO);

}
