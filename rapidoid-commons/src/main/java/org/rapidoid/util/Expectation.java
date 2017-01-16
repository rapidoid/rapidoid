package org.rapidoid.util;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.data.JSON;
import org.rapidoid.u.U;

import java.util.Map;

/*
 * #%L
 * rapidoid-commons
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
@Since("5.2.0")
public class Expectation extends RapidoidThing {

	private final Object target;

	public Expectation(Object target) {
		this.target = target;
	}

	public Expectation entry(String key, Object expectedValue) {
		Object val = asMap().get(key);
		U.must(U.eq(val, expectedValue), "Expected map entry '%s' to have value [%s], but found [%s]!", key, expectedValue, val);
		return this;
	}

	public Expectation value(String expectedValue) {
		String val = as(String.class);
		U.must(U.eq(val, expectedValue), "Expected value [%s], but found [%s]!", expectedValue, val);
		return this;
	}

	private <T> T as(Class<T> type) {
		U.must(Cls.instanceOf(target, type), "Expected a type '%s', but found: %s", type, target);
		return U.cast(target);
	}

	@SuppressWarnings("unchecked")
	private Map<String, ?> asMap() {
		if (target instanceof String) {
			return JSON.parse((String) target);
		}

		return as(Map.class);
	}

	@SuppressWarnings("unchecked")
	public <T> T get() {
		return (T) target;
	}

}
