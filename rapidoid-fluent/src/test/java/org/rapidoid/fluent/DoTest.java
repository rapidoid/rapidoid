package org.rapidoid.fluent;

import org.junit.Test;
import org.rapidoid.test.TestCommons;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class DoTest extends TestCommons {

	@Test
	public void testIterableOps() {
		List<String> items = New.list("a", "bbbbb", "cc");

		List<Integer> lengths2 = Do.map(items).to(s -> s.length());
		eq(lengths2, New.list(1, 5, 2));

		Map<Integer, List<String>> byLengths = Do.group(items).by(s -> s.length());
		eq(byLengths, New.map(1, New.list("a"), 5, New.list("bbbbb"), 2, New.list("cc")));

		String joined = Do.reduce(items).by((a, b) -> a + ":" + b).orElse("");
		eq(joined, "a:bbbbb:cc");

		String joined2 = Do.reduce(items).by("@", (a, b) -> a + ":" + b);
		eq(joined2, "@:a:bbbbb:cc");
	}

	@Test
	public void testMapGroupingBy() {
		Map<Integer, String> nums = New.map(1, "one", 2, "two", 3, "three");

		Map<String, Map<Integer, String>> even = Do.group(nums).by((k, v) -> k % 2 == 0 ? "even" : "odd");

		eq(even, New.map("odd", New.map(1, "one", 3, "three"), "even", New.map(2, "two")));
	}

	@Test
	public void testMapMapping() {
		Map<Integer, String> nums = New.map(1, "one", 2, "two", 3, "three");

		Map<Integer, String> bignums = Do.map(nums).to((k, v) -> k * 1000, (k, v) -> v.toUpperCase());
		eq(bignums, New.map(1000, "ONE", 2000, "TWO", 3000, "THREE"));

		List<Integer> doubleKeys = Do.map(nums).toList((k, v) -> k * 2);
		eq(doubleKeys, New.list(2, 4, 6));

		Set<Integer> valueLengths = Do.map(nums).toSet((k, v) -> v.length());
		eq(valueLengths, New.set(3, 5));
	}

}
