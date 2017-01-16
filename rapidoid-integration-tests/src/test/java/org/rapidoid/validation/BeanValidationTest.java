package org.rapidoid.validation;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.test.TestCommons;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/*
 * #%L
 * rapidoid-integration-tests
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class BeanValidationTest extends TestCommons {

	@Test
	public void testNotNull() {
		Thing thing = new Thing(null, "desc");

		Set<ConstraintViolation<Thing>> violations = Validators.factory().getValidator().validate(thing);

		eq(violations.size(), 1);
		eq(violations.iterator().next().getMessage(), "may not be null");
	}

	@Test
	public void testSize() {
		Thing thing = new Thing("foo", "ab");

		Set<ConstraintViolation<Thing>> violations = Validators.getViolations(thing);

		eq(violations.size(), 1);
		eq(violations.iterator().next().getMessage(), "size must be between 3 and 5");
	}

	@Test
	public void testNoViolations() {
		Thing thing = new Thing("foo", "bar");

		Set<ConstraintViolation<Thing>> violations = Validators.get().validate(thing);

		eq(violations.size(), 0);
	}

	@Test(expected = ConstraintViolationException.class)
	public void testValidationException() {
		Validators.validate(new Thing(null, "ab"));
	}

}

