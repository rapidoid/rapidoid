/*-
 * #%L
 * rapidoid-rest
 * %%
 * Copyright (C) 2014 - 2020 Nikolche Mihajlovski and contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.rapidoid.validation;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.Req;
import org.rapidoid.http.customize.BeanValidator;
import org.rapidoid.u.U;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Iterator;
import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("6.0.0")
public class JavaxBeanValidator extends RapidoidThing implements BeanValidator {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    private final Validator validator = factory.getValidator();

    private <T> Set<ConstraintViolation<T>> getViolations(T bean) {
        return validator.validate(bean);
    }

    static String getValidationErrorMessage(Set<ConstraintViolation<Object>> violations) {
        if (U.isEmpty(violations)) return null; // null means no validation errors!

        StringBuilder sb = new StringBuilder();
        sb.append("Validation failed: ");

        for (Iterator<ConstraintViolation<Object>> it = U.safe(violations).iterator(); it.hasNext(); ) {
            ConstraintViolation<?> v = it.next();

            sb.append(v.getRootBeanClass().getSimpleName());
            sb.append(".");
            sb.append(v.getPropertyPath());
            sb.append(" (");
            sb.append(v.getMessage());
            sb.append(")");

            if (it.hasNext()) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    @Override
    public String validate(Req req, Object bean) {
        Set<ConstraintViolation<Object>> violations = getViolations(bean);

        return getValidationErrorMessage(violations);
    }

}
