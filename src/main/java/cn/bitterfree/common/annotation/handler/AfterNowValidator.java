package cn.bitterfree.common.annotation.handler;

import cn.bitterfree.common.annotation.AfterNow;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Date;
import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-13
 * Time: 1:18
 */
public class AfterNowValidator implements ConstraintValidator<AfterNow, Date> {


    @Override
    public boolean isValid(Date date, ConstraintValidatorContext constraintValidatorContext) {
        return Optional.ofNullable(date).map(Date::getTime).map(time -> {
            return time.compareTo(System.currentTimeMillis()) >= 0;
        }).orElse(Boolean.TRUE);
    }
}
