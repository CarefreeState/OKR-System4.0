package cn.lbcmmszdntnt.common.annotation;

import cn.lbcmmszdntnt.common.annotation.handler.ColorPatternValidator;
import cn.lbcmmszdntnt.common.annotation.handler.IntRangeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-12
 * Time: 19:32
 */
@Documented
@Constraint(validatedBy = {ColorPatternValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ColorPattern {

    String message() default "颜色不合法"; // 默认消息

    Class<?>[] groups() default {}; // 分组校验

    Class<? extends Payload>[] payload() default {}; // 负载信息
}
