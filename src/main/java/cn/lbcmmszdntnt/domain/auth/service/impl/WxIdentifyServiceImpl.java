package cn.lbcmmszdntnt.domain.auth.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.auth.service.ValidateService;
import cn.lbcmmszdntnt.domain.auth.service.WxIdentifyService;
import cn.lbcmmszdntnt.wxtoken.model.dto.JsCode2SessionDTO;
import cn.lbcmmszdntnt.wxtoken.model.vo.JsCode2SessionVO;
import cn.lbcmmszdntnt.wxtoken.util.WxHttpRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static cn.lbcmmszdntnt.domain.auth.constants.AuthConstants.VALIDATE_WX_CODE_KEY;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 17:20
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WxIdentifyServiceImpl implements WxIdentifyService {

    private final ValidateService validateService;

    @Override
    public JsCode2SessionVO validateCode(String code) {
        // 构造请求 + 发起请求 -> code2Session
        JsCode2SessionDTO jsCode2SessionDTO = JsCode2SessionDTO.builder().jsCode(code).build();
        JsCode2SessionVO jsCode2SessionVO = WxHttpRequestUtil.jsCode2Session(jsCode2SessionDTO);
        validateService.validate(VALIDATE_WX_CODE_KEY + code, () -> {
            return Objects.nonNull(jsCode2SessionVO.getOpenid());
        }, GlobalServiceStatusCode.WX_CODE_NOT_VALID);
        return jsCode2SessionVO;
    }
}
