package cn.bitterfree.api.common.util.convert;

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
public class MakeUpUtil {

    private static void makeUp(List<Runnable> behaviorList, Class<? extends Exception> checkedExceptionClazz) {
        ObjectUtil.nonNullstream(behaviorList).forEach(makeUp -> {
            tryDoSomething(makeUp, checkedExceptionClazz, Boolean.FALSE);
        });
    }

    public static void tryDoSomething(Runnable behavior, Class<? extends Exception> checkedExceptionClazz, Boolean again, Runnable... makeUp) {
        try {
            behavior.run();
        } catch (Exception e) {
            log.error(e.getMessage());
            if(Boolean.TRUE.equals(again) && checkedExceptionClazz.isInstance(e)) {
                log.warn("开始弥补...");
                makeUp(List.of(makeUp), checkedExceptionClazz);
                log.warn("重新执行...");
                behavior.run();
            }
        }
    }

    public static <T> T tryGetSomething(Supplier<T> supplier, Class<? extends Exception> checkedExceptionClazz, Boolean again, Runnable... makeUp) {
        try {
            return supplier.get();
        } catch (Exception e) {
            log.error(e.getMessage());
            if(Boolean.TRUE.equals(again) && checkedExceptionClazz.isInstance(e)) {
                log.warn("开始弥补...");
                makeUp(List.of(makeUp), checkedExceptionClazz);
                log.warn("重新执行...");
                return supplier.get();
            }
            return null;
        }
    }

}
