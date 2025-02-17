package cn.bitterfree.api.common.util.validation;

import cn.hutool.extra.spring.SpringUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import java.util.Set;

public class ValidatorUtils {

	private final static Validator VALIDATOR = SpringUtil.getBean(Validator.class);

	public static <T> void validate(T object, Class<?>... groups) {
		Set<ConstraintViolation<T>> validate = VALIDATOR.validate(object, groups);
		if (!validate.isEmpty()) {
			String message = String.format("请求对象:'%s'", object.toString());
			throw new ConstraintViolationException(message, validate);
		}
	}

}