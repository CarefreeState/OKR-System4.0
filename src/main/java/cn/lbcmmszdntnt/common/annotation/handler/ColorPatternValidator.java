package cn.lbcmmszdntnt.common.annotation.handler;

import cn.lbcmmszdntnt.common.annotation.ColorPattern;
import cn.lbcmmszdntnt.common.annotation.IntRange;
import cn.lbcmmszdntnt.domain.core.model.po.inner.StatusFlag;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-12
 * Time: 19:33
 */
public class ColorPatternValidator implements ConstraintValidator<ColorPattern, String> {

    public final static String COLOR_PATTERN = "^#([0-9a-fA-F]{6}|[0-9a-fA-F]{3})$";

    @Override
    public boolean isValid(String color, ConstraintValidatorContext constraintValidatorContext) {
        return StringUtils.hasText(color) && color.matches(COLOR_PATTERN);
    }
}
