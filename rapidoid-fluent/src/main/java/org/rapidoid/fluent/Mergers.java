package org.rapidoid.fluent;

import java.util.function.BinaryOperator;

/*
 * #%L
 * rapidoid-fluent
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

/**
 * @author Nikolche Mihajlovski
 * @since 5.2.0
 */
public class Mergers {

	public static <T> BinaryOperator<T> thrower() {
		return (oldValue, newValue) -> {
			throw new IllegalStateException(String.format("Duplicate key! Values are: %s and %s", oldValue, newValue));
		};
	}

	public static <T> BinaryOperator<T> keeper() {
		return (oldValue, newValue) -> oldValue;
	}

	public static <T> BinaryOperator<T> replacer() {
		return (oldValue, newValue) -> newValue;
	}

}
