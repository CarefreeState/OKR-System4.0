package cn.bitterfree.domain.login.service.impl;

import cn.bitterfree.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.common.exception.GlobalServiceException;
import cn.bitterfree.domain.auth.service.PasswordIdentifyService;
import cn.bitterfree.domain.login.model.dto.LoginDTO;
import cn.bitterfree.domain.login.model.dto.PasswordLoginDTO;
import cn.bitterfree.domain.login.service.LoginService;
import cn.bitterfree.domain.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 1:35
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordLoginServiceImpl implements LoginService {

    private final PasswordIdentifyService passwordIdentifyService;

    @Override
    public User login(LoginDTO loginDTO) {
        PasswordLoginDTO passwordParams = Optional.ofNullable(loginDTO.getPasswordLoginDTO()).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.PARAM_IS_BLANK));
        return passwordIdentifyService.validatePassword(passwordParams.getUsername(), passwordParams.getPassword());
    }
}
