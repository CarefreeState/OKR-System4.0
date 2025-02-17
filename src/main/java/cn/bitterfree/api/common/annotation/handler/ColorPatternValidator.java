package cn.bitterfree.api.common.annotation.handler;

import cn.bitterfree.api.common.annotation.ColorPattern;
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

    private final static String COLOR_PATTERN = "^#([0-9a-fA-F]{6}|[0-9a-fA-F]{3})$";

    @Override
    public boolean isValid(String color, ConstraintValidatorContext constraintValidatorContext) {
        // NotBlank + Pattern 的效果
        return StringUtils.hasText(color) && color.matches(COLOR_PATTERN);
    }
}
