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

import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.customize.BeanValidator;
import org.rapidoid.test.TestCommons;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class BeanValidationTest extends TestCommons {

    @Test
    public void testNotNull() {
        Thing thing = new Thing(null, "desc");

        BeanValidator validator = new JavaxBeanValidator();
        String msg = validator.validate(null, thing);

        eq(msg, "Validation failed: Thing.name (must not be null)");
    }

    @Test
    public void testSize() {
        Thing thing = new Thing("foo", "ab");

        BeanValidator validator = new JavaxBeanValidator();
        String msg = validator.validate(null, thing);

        eq(msg, "Validation failed: Thing.desc (size must be between 3 and 5)");
    }

    @Test
    public void testNoViolations() {
        Thing thing = new Thing("foo", "bar");

        BeanValidator validator = new JavaxBeanValidator();
        String msg = validator.validate(null, thing);

        isNull(msg);
    }

}

