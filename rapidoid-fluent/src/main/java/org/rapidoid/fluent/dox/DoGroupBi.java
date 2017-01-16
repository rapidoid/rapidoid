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

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author Nikolche Mihajlovski
 * @since 5.0.0
 */
public class DoGroupBi<K, V> {

	private final Map<K, V> items;

	public DoGroupBi(Map<K, V> items) {
		this.items = items;
	}

	public <R> Map<R, Map<K, V>> by(BiFunction<K, V, R> transformation) {
		Function<Entry<K, V>, R> classifier = e -> transformation.apply(e.getKey(), e.getValue());
		Collector<Entry<K, V>, ?, Map<K, V>> downstream = To.linkedMap(e -> e.getKey(), e -> e.getValue());
		return items.entrySet().stream().collect(Collectors.groupingBy(classifier, downstream));
	}

}
