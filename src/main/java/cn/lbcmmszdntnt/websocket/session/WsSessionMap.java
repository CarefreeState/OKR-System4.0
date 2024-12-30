package cn.lbcmmszdntnt.websocket.session;

import cn.lbcmmszdntnt.common.util.session.SessionMap;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;


/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-26
 * Time: 16:37
 */
@Repository
@Slf4j
public class WsSessionMap extends SessionMap<Session> {

}
