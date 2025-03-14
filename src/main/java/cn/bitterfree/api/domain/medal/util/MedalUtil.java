package cn.bitterfree.api.domain.medal.util;

import lombok.extern.slf4j.Slf4j;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 12:39
 */
@Slf4j
public class MedalUtil {

    public static double log2(double base) {
        return Math.log(base) / Math.log(2);
    }

    public static Integer getLevel(Long credit, Integer coefficient) {
        double base = credit * 1.0 / coefficient;
        base = base < 1 ? 0.5 : base;
        int level = (int) log2(base) + 1;
        log.info("积分 {} 基数 {} 计算等级 -> {}", credit, coefficient, level);
        return level;
    }
}
