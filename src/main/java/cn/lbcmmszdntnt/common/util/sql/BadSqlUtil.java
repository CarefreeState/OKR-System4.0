package cn.lbcmmszdntnt.common.util.sql;

import cn.lbcmmszdntnt.common.util.convert.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.BadSqlGrammarException;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-11-19
 * Time: 22:29
 */
@Slf4j
public class BadSqlUtil {

    private static void makeUp(List<Runnable> behaviorList) {
        ObjectUtil.nonNullstream(behaviorList).forEach(makeUp -> {
            tryDoSomething(makeUp, Boolean.FALSE);
        });
    }

    public static void tryDoSomething(Runnable behavior, Boolean again, Runnable... makeUp) {
        try {
            behavior.run();
        } catch (BadSqlGrammarException e) {
            log.error(e.getMessage());
            if(Boolean.TRUE.equals(again)) {
                log.warn("开始弥补...");
                makeUp(List.of(makeUp));
                log.warn("重新执行...");
                behavior.run();
            }
        }
    }

    public static <T> T tryGetSomething(Supplier<T> supplier, Boolean again, Runnable... makeUp) {
        try {
            return supplier.get();
        } catch (BadSqlGrammarException e) {
            log.error(e.getMessage());
            if(Boolean.TRUE.equals(again)) {
                log.warn("开始弥补...");
                makeUp(List.of(makeUp));
                log.warn("重新执行...");
                return supplier.get();
            }
            return null;
        }
    }

}
