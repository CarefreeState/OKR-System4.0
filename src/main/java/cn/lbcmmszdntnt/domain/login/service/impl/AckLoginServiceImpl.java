package cn.lbcmmszdntnt.domain.login.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.common.exception.GlobalServiceException;
import cn.lbcmmszdntnt.domain.auth.service.LoginAckIdentifyService;
import cn.lbcmmszdntnt.domain.login.model.dto.AckLoginDTO;
import cn.lbcmmszdntnt.domain.login.model.dto.LoginDTO;
import cn.lbcmmszdntnt.domain.login.service.LoginService;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    private final LoginAckIdentifyService loginAckIdentifyService;

    @Override
    public User login(LoginDTO loginDTO) {
        AckLoginDTO ackLoginDTO = Optional.ofNullable(loginDTO.getAckLoginDTO()).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.PARAM_IS_BLANK));
        return loginAckIdentifyService.validateSecret(ackLoginDTO.getSecret());
    }
}
