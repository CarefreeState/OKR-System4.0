package cn.bitterfree.api.wxtoken.client;

import cn.bitterfree.api.wxtoken.model.dto.AccessTokenDTO;
import cn.bitterfree.api.wxtoken.model.dto.WxQRCode;
import cn.bitterfree.api.wxtoken.model.vo.AccessTokenVO;
import cn.bitterfree.api.wxtoken.model.vo.JsCode2SessionVO;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-03-05
 * Time: 13:35
 */
@FeignClient(name = "wx-service", url = "https://api.weixin.qq.com")
public interface WxHttpClient {

    @PostMapping("/cgi-bin/stable_token")
    AccessTokenVO stableToken(@RequestBody AccessTokenDTO accessTokenDTO);

    @GetMapping("/sns/jscode2session")
    JsCode2SessionVO jscode2session(@RequestParam("appid") String appid,
                                    @RequestParam("secret") String secret,
                                    @RequestParam("js_code") String jsCode,
                                    @RequestParam("grant_type") String grantType);

    @PostMapping("/wxa/getwxacodeunlimit")
    Response getwxacodeunlimit(@RequestParam("access_token") String accessToken,
                               @RequestBody WxQRCode wxQRCode); // 用 Response 接受二进制数据


}
