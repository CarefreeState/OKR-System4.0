package cn.bitterfree.domain.medal.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-13
 * Time: 0:03
 */
@Getter
@AllArgsConstructor
public enum MedalType {

    // 初心启航
    STAY_TRUE_BEGINNING(1L, 1),
    // 硕果累累
    HARVEST_ACHIEVEMENT(2L, 60),
    // 出类拔萃
    STAND_OUT_CROWD(3L, 1),
    // 胜券在握
    VICTORY_WITHIN_GRASP(4L, 1),
    // 短期达标
    SHORT_TERM_ACHIEVEMENT(5L, 5),
    // 长久有成
    LONG_TERM_ACHIEVEMENT(6L, 2),
    // 渐入佳境
    GREAT_STATE(7L, 1),

    ;

    @JsonValue
    private final Long medalId;
    private final Integer coefficient;

}
