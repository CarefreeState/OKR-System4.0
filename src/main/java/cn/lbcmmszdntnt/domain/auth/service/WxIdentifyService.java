package cn.lbcmmszdntnt.domain.auth.service;

import cn.lbcmmszdntnt.wxtoken.model.vo.JsCode2SessionVO;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 1:54
 */
public interface WxIdentifyService {

    // 获取 code 的方法在客户端不在服务端
//     String getCode();

    // 校验 code
    JsCode2SessionVO validateCode(String code);

}
