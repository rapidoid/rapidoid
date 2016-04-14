package org.rapidoid.http.customize;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.UTILS;
import org.rapidoid.validation.Validators;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class DefaultBeanValidator implements BeanValidator {

	private final boolean supported = UTILS.hasValidation();

	@Override
	public void validate(Object bean) {
		if (supported) {
			Validators.validate(bean);
		}
	}

}
