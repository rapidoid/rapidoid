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

import org.junit.Before;
import org.junit.Test;
import org.rapidoid.fluent.flow.FlowImpl;
import org.rapidoid.test.TestCommons;

import java.util.stream.Stream;

/**
 * @author Nikolche Mihajlovski
 * @since 5.0.0
 */
public class FlowAsStreamDecoratorTest extends TestCommons {

	private Stream<Object> stream;

	private FlowImpl<Object> flow;

	@SuppressWarnings("unchecked")
	@Before
	public void init() {
		stream = mock(Stream.class);
		flow = new FlowImpl<Object>(stream);
	}

	@Test
	public final void testIterator() {
		flow.iterator();
		verify(stream).iterator();
	}

	@Test
	public final void testSpliterator() {
		flow.spliterator();
		verify(stream).spliterator();
	}

	@Test
	public final void testIsParallel() {
		flow.isParallel();
		verify(stream).isParallel();
	}

	@Test
	public final void testSequential() {
		flow.sequential();
		verify(stream).sequential();
	}

	@Test
	public final void testParallel() {
		flow.parallel();
		verify(stream).parallel();
	}

	@Test
	public final void testUnordered() {
		flow.unordered();
		verify(stream).unordered();
	}

	@Test
	public final void testOnClose() {
		flow.onClose(null);
		verify(stream).onClose(null);
	}

	@Test
	public final void testClose() {
		flow.close();
		verify(stream).close();
	}

	@Test
	public final void testFilter() {
		flow.filter(null);
		verify(stream).filter(null);
	}

	@Test
	public final void testMap() {
		flow.map(null);
		verify(stream).map(null);
	}

	@Test
	public final void testMapToInt() {
		flow.mapToInt(null);
		verify(stream).mapToInt(null);
	}

	@Test
	public final void testMapToLong() {
		flow.mapToLong(null);
		verify(stream).mapToLong(null);
	}

	@Test
	public final void testMapToDouble() {
		flow.mapToDouble(null);
		verify(stream).mapToDouble(null);
	}

	@Test
	public final void testFlatMap() {
		flow.flatMap(null);
		verify(stream).flatMap(null);
	}

	@Test
	public final void testFlatMapToInt() {
		flow.flatMapToInt(null);
		verify(stream).flatMapToInt(null);
	}

	@Test
	public final void testFlatMapToLong() {
		flow.flatMapToLong(null);
		verify(stream).flatMapToLong(null);
	}

	@Test
	public final void testFlatMapToDouble() {
		flow.flatMapToDouble(null);
		verify(stream).flatMapToDouble(null);
	}

	@Test
	public final void testDistinct() {
		flow.distinct();
		verify(stream).distinct();
	}

	@Test
	public final void testSorted() {
		flow.sorted(null);
		verify(stream).sorted(null);
	}

	@Test
	public final void testSorted2() {
		flow.sorted(null);
		verify(stream).sorted(null);
	}

	@Test
	public final void testPeek() {
		flow.peek(null);
		verify(stream).peek(null);
	}

	@Test
	public final void testLimit() {
		flow.limit(5);
		verify(stream).limit(5);
	}

	@Test
	public final void testSkip() {
		flow.skip(3);
		verify(stream).skip(3);
	}

	@Test
	public final void testForEach() {
		flow.forEach(null);
		verify(stream).forEach(null);
	}

	@Test
	public final void testForEachOrdered() {
		flow.forEachOrdered(null);
		verify(stream).forEachOrdered(null);
	}

	@Test
	public final void testToArray() {
		flow.toArray(null);
		verify(stream).toArray(null);
	}

	@Test
	public final void testToArray2() {
		flow.toArray(null);
		verify(stream).toArray(null);
	}

	@Test
	public final void testReduce1() {
		flow.reduce(null);
		verify(stream).reduce(null);
	}

	@Test
	public final void testReduce2() {
		flow.reduce(null, null);
		verify(stream).reduce(null, null);
	}

	@Test
	public final void testReduce3() {
		flow.reduce(null, null, null);
		verify(stream).reduce(null, null, null);
	}

	@Test
	public final void testCollect() {
		flow.collect(null);
		verify(stream).collect(null);
	}

	@Test
	public final void testCollectCollectorOfQsuperTAR() {
		flow.collect(null);
		verify(stream).collect(null);
	}

	@Test
	public final void testMin() {
		flow.min(null);
		verify(stream).min(null);
	}

	@Test
	public final void testMax() {
		flow.max(null);
		verify(stream).max(null);
	}

	@Test
	public final void testCount() {
		flow.count();
		verify(stream).count();
	}

	@Test
	public final void testAnyMatch() {
		flow.anyMatch(null);
		verify(stream).anyMatch(null);
	}

	@Test
	public final void testAllMatch() {
		flow.allMatch(null);
		verify(stream).allMatch(null);
	}

	@Test
	public final void testNoneMatch() {
		flow.noneMatch(null);
		verify(stream).noneMatch(null);
	}

	@Test
	public final void testFindFirst() {
		flow.findFirst();
		verify(stream).findFirst();
	}

	@Test
	public final void testFindAny() {
		flow.findAny();
		verify(stream).findAny();
	}

	@Test
	public final void testStream() {
		eq(flow.stream(), stream);
	}

}
