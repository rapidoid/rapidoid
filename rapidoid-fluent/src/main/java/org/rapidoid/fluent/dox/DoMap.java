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

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Nikolche Mihajlovski
 * @since 5.0.0
 */
public class DoMap<T> {

	private final Stream<T> stream;

	public DoMap(Stream<T> stream) {
		this.stream = stream;
	}

	public <R> List<R> to(Function<T, R> transformation) {
		return stream.map(transformation).collect(To.list());
	}

	public <R> List<R> to(BiFunction<Integer, T, R> indexAwareTransformation) {
		AtomicInteger index = new AtomicInteger();
		return stream.map(item -> indexAwareTransformation.apply(index.getAndIncrement(), item)).collect(To.list());
	}

	public <K, V> Map<K, V> to(Function<T, K> keyMapper, Function<T, V> valueMapper) {
		return stream.collect(To.linkedMap(keyMapper, valueMapper));
	}

}
