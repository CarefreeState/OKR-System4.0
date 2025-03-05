package cn.bitterfree.api.domain.auth.service.impl;

import cn.bitterfree.api.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.api.domain.auth.constants.AuthConstants;
import cn.bitterfree.api.domain.auth.service.ValidateService;
import cn.bitterfree.api.domain.auth.service.WxIdentifyService;
import cn.bitterfree.api.wxtoken.model.vo.JsCode2SessionVO;
import cn.bitterfree.api.wxtoken.util.WxHttpRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
        JsCode2SessionVO jsCode2SessionVO = WxHttpRequestUtil.jsCode2Session(code);
        validateService.validate(AuthConstants.VALIDATE_WX_CODE_KEY + code, () -> {
            return Objects.nonNull(jsCode2SessionVO.getOpenid());
        }, GlobalServiceStatusCode.WX_CODE_NOT_VALID);
        return jsCode2SessionVO;
    }
}
