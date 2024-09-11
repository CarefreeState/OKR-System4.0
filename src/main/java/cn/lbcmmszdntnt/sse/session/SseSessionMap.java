package cn.lbcmmszdntnt.sse.session;


import cn.lbcmmszdntnt.util.session.SessionMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-26
 * Time: 16:37
 */
@Repository
@Slf4j
public class SseSessionMap extends SessionMap<SseEmitter> {
}
