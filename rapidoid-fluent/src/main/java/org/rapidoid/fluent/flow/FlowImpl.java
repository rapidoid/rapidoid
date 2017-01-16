package org.rapidoid.fluent.flow;

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

import org.rapidoid.fluent.Do;
import org.rapidoid.fluent.Flow;
import org.rapidoid.fluent.To;
import org.rapidoid.fluent.utils.StreamUtils;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * @author Nikolche Mihajlovski
 * @since 5.0.0
 */
public class FlowImpl<T> implements Flow<T> {

	private final Stream<T> stream;

	public FlowImpl(Stream<T> stream) {
		this.stream = stream;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<T> iterator() {
		return stream.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Spliterator<T> spliterator() {
		return stream.spliterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isParallel() {
		return stream.isParallel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FlowImpl<T> sequential() {
		return new FlowImpl<T>(stream.sequential());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FlowImpl<T> parallel() {
		return new FlowImpl<T>(stream.parallel());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FlowImpl<T> unordered() {
		return new FlowImpl<T>(stream.unordered());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FlowImpl<T> onClose(Runnable closeHandler) {
		return new FlowImpl<T>(stream.onClose(closeHandler));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		stream.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FlowImpl<T> filter(Predicate<? super T> predicate) {
		return new FlowImpl<T>(stream.filter(predicate));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <R> FlowImpl<R> map(Function<? super T, ? extends R> mapper) {
		return new FlowImpl<R>(stream.map(mapper));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IntStream mapToInt(ToIntFunction<? super T> mapper) {
		return stream.mapToInt(mapper);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LongStream mapToLong(ToLongFunction<? super T> mapper) {
		return stream.mapToLong(mapper);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
		return stream.mapToDouble(mapper);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <R> FlowImpl<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
		return new FlowImpl<R>(stream.flatMap(mapper));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
		return stream.flatMapToInt(mapper);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
		return stream.flatMapToLong(mapper);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
		return stream.flatMapToDouble(mapper);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FlowImpl<T> distinct() {
		return new FlowImpl<T>(stream.distinct());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FlowImpl<T> sorted() {
		return new FlowImpl<T>(stream.sorted());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FlowImpl<T> sorted(Comparator<? super T> comparator) {
		return new FlowImpl<T>(stream.sorted(comparator));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FlowImpl<T> peek(Consumer<? super T> action) {
		return new FlowImpl<T>(stream.peek(action));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FlowImpl<T> limit(long maxSize) {
		return new FlowImpl<T>(stream.limit(maxSize));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FlowImpl<T> skip(long n) {
		return new FlowImpl<T>(stream.skip(n));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void forEach(Consumer<? super T> action) {
		stream.forEach(action);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void forEachOrdered(Consumer<? super T> action) {
		stream.forEachOrdered(action);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] toArray() {
		return stream.toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <A> A[] toArray(IntFunction<A[]> generator) {
		return stream.toArray(generator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T reduce(T identity, BinaryOperator<T> accumulator) {
		return stream.reduce(identity, accumulator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<T> reduce(BinaryOperator<T> accumulator) {
		return stream.reduce(accumulator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
		return stream.reduce(identity, accumulator, combiner);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
		return stream.collect(supplier, accumulator, combiner);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <R, A> R collect(Collector<? super T, A, R> collector) {
		return stream.collect(collector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<T> min(Comparator<? super T> comparator) {
		return stream.min(comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<T> max(Comparator<? super T> comparator) {
		return stream.max(comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long count() {
		return stream.count();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean anyMatch(Predicate<? super T> predicate) {
		return stream.anyMatch(predicate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean allMatch(Predicate<? super T> predicate) {
		return stream.allMatch(predicate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean noneMatch(Predicate<? super T> predicate) {
		return stream.noneMatch(predicate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<T> findFirst() {
		return stream.findFirst();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<T> findAny() {
		return stream.findAny();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> toList() {
		return stream.collect(To.list());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<T> toSet() {
		return stream.collect(To.set());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <K, V> Map<K, V> toMap(Function<T, K> keyTransformation, Function<T, V> valueTransformation) {
		return stream.collect(To.map(keyTransformation, valueTransformation));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Stream<T> stream() {
		return stream;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <R> Flow<T> withNonNull(Function<? super T, R> transformation) {
		return new FlowImpl<>(stream.filter(x -> transformation.apply(x) != null));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <R> Flow<T> withNull(Function<? super T, R> transformation) {
		return new FlowImpl<>(stream.filter(x -> transformation.apply(x) == null));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <K, V> Map<K, List<T>> groupBy(Function<T, K> transformation) {
		return stream.collect(Collectors.groupingBy(transformation));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Flow<T> reverse() {
		List<T> list = toList();
		Collections.reverse(list);
		return new FlowImpl<>(Do.stream(list));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<T> findLast() {
		return StreamUtils.findLastOf(stream);
	}

}
