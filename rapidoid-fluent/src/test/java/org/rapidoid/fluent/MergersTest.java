package org.rapidoid.fluent;

import org.junit.Test;
import org.rapidoid.test.TestCommons;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
public class MergersTest extends TestCommons {

	@Test(expected = IllegalStateException.class)
	public void testThrowerMergerAsDefault() {

		List<String> words = New.list("a", "bb", "ccc", "bb");
		AtomicInteger n = new AtomicInteger();

		Map<String, Integer> lengths = Do.map(words).to(x -> x, x -> n.incrementAndGet());

		eq(lengths, New.map("a", 1, "bb", 2, "ccc", 3));
	}

	@Test(expected = IllegalStateException.class)
	public void testThrowerMerger() {

		List<String> words = New.list("a", "bb", "ccc", "bb");
		AtomicInteger n = new AtomicInteger();

		Map<String, Integer> lengths = words.stream()
			.collect(To.map(x -> x, x -> n.incrementAndGet(), Mergers.thrower(), LinkedHashMap::new));

		eq(lengths, New.map("a", 1, "bb", 2, "ccc", 3));
	}

	@Test
	public void testKeeperMerger() {

		List<String> words = New.list("a", "bb", "ccc", "bb");
		AtomicInteger n = new AtomicInteger();

		Map<String, Integer> lengths = words.stream()
			.collect(To.linkedMap(x -> x, x -> n.incrementAndGet(), Mergers.keeper()));

		eq(lengths, New.map("a", 1, "bb", 2, "ccc", 3));
	}

	@Test
	public void testReplacerMerger() {

		List<String> words = New.list("a", "bb", "ccc", "bb");
		AtomicInteger n = new AtomicInteger();

		Map<String, Integer> lengths = words.stream()
			.collect(To.map(x -> x, x -> n.incrementAndGet(), Mergers.replacer()));

		eq(lengths, New.map("a", 1, "bb", 4, "ccc", 3));
	}

}
