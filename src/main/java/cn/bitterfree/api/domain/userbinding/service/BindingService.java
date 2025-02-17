package cn.bitterfree.api.domain.userbinding.service;

import cn.bitterfree.api.domain.user.model.entity.User;
import cn.bitterfree.api.domain.userbinding.model.dto.BindingDTO;

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
