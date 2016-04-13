package org.rapidoid.validation;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Validators {

	private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

	private static final Validator validator = factory.getValidator();

	public static ValidatorFactory factory() {
		return factory;
	}

	public static Validator get() {
		return validator;
	}

	public static <T> Set<ConstraintViolation<T>> validate(T bean) {
		return validator.validate(bean);
	}

}
