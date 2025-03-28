package cn.bitterfree.api.common.annotation.handler;

import cn.bitterfree.api.common.annotation.IntRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-08-07
 * Time: 17:34
 */
public class IntRangeValidator implements ConstraintValidator<IntRange, Object> {

    private int min;

    private int max;

    @Override
    public void initialize(IntRange intRange) {
        this.min = intRange.min();
        this.max = intRange.max();
    }

    private int compare(Number number1, Number number2) {
        return Double.compare(number1.doubleValue(), number2.doubleValue());
    }

    private boolean isValid(Object value) {
        if(Objects.isNull(value)) {
            return Boolean.TRUE;
        } else if (value instanceof Number number) {
            return compare(number, min) >= 0 && compare(number, max) <= 0;
        } else if (value instanceof Collection<?> collection) {
            return collection.stream().allMatch(this::isValid);
        } else if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                if(!isValid(Array.get(value, i))) {
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return isValid(value);
    }
}
