package org.rapidoid.fluent;

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

import org.rapidoid.fluent.flow.FlowImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * A Flow is a {@link Stream} decorator with extra operations for convenience.
 *
 * @author Nikolche Mihajlovski
 * @since 5.0.0
 */
public interface Flow<T> extends Stream<T> {

	@SafeVarargs
	@SuppressWarnings("unchecked")
	static <T> FlowImpl<T> of(T... values) {
		return new FlowImpl<T>(Do.streamOf(values));
	}

	static <T> FlowImpl<T> of(Iterable<T> values) {
		return new FlowImpl<T>(Do.stream(values));
	}

	static <T> FlowImpl<T> of(Stream<T> values) {
		return new FlowImpl<T>(Do.stream(values));
	}

	static FlowImpl<Long> range(long startInclusive, long endExclusive) {
		return new FlowImpl<Long>(LongStream.range(startInclusive, endExclusive).boxed());
	}

	static FlowImpl<Long> count(long startInclusive, long endInclusive) {
		return new FlowImpl<Long>(LongStream.rangeClosed(startInclusive, endInclusive).boxed());
	}

	static FlowImpl<Character> chars(char startInclusive, char endInclusive) {
		return count(startInclusive, endInclusive).map(n -> (char) n.intValue());
	}

	/**
	 * Returns the wrapped stream.
	 */
	Stream<T> stream();

	/**
	 * Equivalent to <code>collect(Collectors.toList())</code>.
	 */
	List<T> toList();

	/**
	 * Equivalent to <code>collect(Collectors.toSet())</code>.
	 */
	Set<T> toSet();

	/**
	 * Equivalent to <code>collect(Collectors.toMap(keyTransformation, valueTransformation))</code>.
	 */
	<K, V> Map<K, V> toMap(Function<T, K> keyTransformation, Function<T, V> valueTransformation);

	/**
	 * Equivalent to <code>stream.filter(x -> transformation.apply(x) != null))</code>.
	 */
	<R> Flow<T> withNonNull(Function<? super T, R> transformation);

	/**
	 * Equivalent to <code>filter(x -> transformation.apply(x) == null)</code>.
	 */
	<R> Flow<T> withNull(Function<? super T, R> transformation);

	/**
	 * Equivalent to <code>collect(Collectors.groupingBy(transformation))</code>.
	 */
	<K, V> Map<K, List<T>> groupBy(Function<T, K> transformation);

	/**
	 * Reverses the order of the elements in the flow.
	 */
	Flow<T> reverse();

	/**
	 * Returns an {@link Optional} describing the last element of this stream, or an empty {@code Optional} if the
	 * stream is empty. If the stream has no encounter order, then any element may be returned.
	 */
	Optional<T> findLast();

}
