package cn.lbcmmszdntnt.domain.auth.service.impl;

import cn.lbcmmszdntnt.domain.auth.model.dto.LoginDTO;
import cn.lbcmmszdntnt.domain.auth.service.LoginService;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-09
 * Time: 17:39
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AckLoginServiceImpl implements LoginService {

    @Override
    public User login(LoginDTO loginDTO) {
        return null;
    }
}
