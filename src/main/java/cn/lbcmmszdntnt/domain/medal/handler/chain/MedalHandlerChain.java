package cn.lbcmmszdntnt.domain.medal.handler.chain;

import cn.lbcmmszdntnt.domain.medal.handler.ApplyMedalHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 16:03
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MedalHandlerChain extends ApplyMedalHandler {

    private final List<ApplyMedalHandler> applyMedalHandlers;

    private ApplyMedalHandler initHandlerChain() {
        int size = applyMedalHandlers.size();
        if(size == 0) {
            return null;
        }
        for (int i = 0; i < size - 1; i++) {
            applyMedalHandlers.get(i).setNextHandler(applyMedalHandlers.get(i + 1));
        }
        return applyMedalHandlers.get(0);
    }

    @PostConstruct
    public void doPostConstruct() {
        this.setNextHandler(initHandlerChain());
    }

    @Override
    public void handle(Object object) {
        super.doNextHandler(object);
        log.warn("责任链处理完毕！");
    }
}
