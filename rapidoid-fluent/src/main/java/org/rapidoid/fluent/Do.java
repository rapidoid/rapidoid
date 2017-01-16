package org.rapidoid.fluent;

import org.rapidoid.fluent.dox.*;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
public class Do {

	public static <T> Stream<T> stream(Iterable<T> items) {
		return items != null ? StreamSupport.stream(items.spliterator(), false) : Stream.empty();
	}

	public static <T> Stream<T> stream(Stream<T> stream) {
		return stream != null ? stream : Stream.empty();
	}

	@SafeVarargs
	@SuppressWarnings("unchecked")
	public static <T> Stream<T> streamOf(T... items) {
		return items != null ? Stream.of(items) : Stream.empty();
	}

	@SafeVarargs
	@SuppressWarnings("unchecked")
	public static <T> DoMap<T> map(T... items) {
		return new DoMap<T>(streamOf(items));
	}

	public static <T> DoMap<T> map(Iterable<T> items) {
		return new DoMap<T>(stream(items));
	}

	@SuppressWarnings("unchecked")
	public static <K, V> DoMapBi<K, V> map(Map<K, V> items) {
		return new DoMapBi<K, V>(items != null ? items : Collections.EMPTY_MAP);
	}

	public static <T> DoGroup<T> group(Iterable<T> items) {
		return new DoGroup<T>(stream(items));
	}

	@SuppressWarnings("unchecked")
	public static <K, V> DoGroupBi<K, V> group(Map<K, V> items) {
		return new DoGroupBi<K, V>(items != null ? items : Collections.EMPTY_MAP);
	}

	public static <T> DoReduce<T> reduce(Iterable<T> items) {
		return new DoReduce<T>(stream(items));
	}

}
