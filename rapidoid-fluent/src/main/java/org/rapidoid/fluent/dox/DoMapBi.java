package org.rapidoid.fluent.dox;

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

import org.rapidoid.fluent.To;
import org.rapidoid.fluent.utils.Lambdas;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * @author Nikolche Mihajlovski
 * @since 5.0.0
 */
public class DoMapBi<K, V> {

	private final Map<K, V> items;

	public DoMapBi(Map<K, V> items) {
		this.items = items;
	}

	public <K2, V2> Map<K2, V2> to(BiFunction<K, V, ? extends K2> keyTransformation,
	                               BiFunction<K, V, ? extends V2> valueTransformation) {
		return items.entrySet().stream()
			.collect(To.linkedMap(e -> Lambdas.apply(e, keyTransformation), e -> Lambdas.apply(e, valueTransformation)));
	}

	public <R> List<R> toList(BiFunction<K, V, R> transformation) {
		return items.entrySet().stream().map(e -> Lambdas.apply(e, transformation)).collect(To.list());
	}

	public <R> Set<R> toSet(BiFunction<K, V, R> transformation) {
		return items.entrySet().stream().map(e -> Lambdas.apply(e, transformation)).collect(To.set());
	}

}
