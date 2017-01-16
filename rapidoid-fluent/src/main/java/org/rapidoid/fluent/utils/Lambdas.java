package org.rapidoid.fluent.utils;

import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

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
 * @since 5.0.0
 */
public class Lambdas {

	public static <K, V> Predicate<? super Entry<K, V>> entryTest(BiPredicate<K, V> predicate) {
		return e -> predicate.test(e.getKey(), e.getValue());
	}

	public static <K, V, R> R apply(Entry<K, V> e, BiFunction<K, V, R> transformation) {
		return transformation.apply(e.getKey(), e.getValue());
	}

}
