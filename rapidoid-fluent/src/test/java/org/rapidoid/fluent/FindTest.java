package org.rapidoid.fluent;

import org.junit.Test;
import org.rapidoid.test.TestCommons;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

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
public class FindTest extends TestCommons {

	@Test
	public void testFindOps() {
		List<String> items = New.list("a", "bbbbb", "cc");

		Optional<String> firstSmall = Find.firstOf(items).where(s -> s.length() < 3);
		eq(firstSmall.get(), "a");

		Optional<String> anyBig = Find.lastOf(items).where(s -> s.length() > 4);
		eq(anyBig.get(), "bbbbb");

		Optional<String> big = Find.anyOf(items).where(s -> s.length() == 5);
		eq(big.get(), "bbbbb");

		List<String> notSmall = Find.allOf(items).where(s -> s.length() > 1);
		eq(notSmall, New.list("bbbbb", "cc"));

		isTrue(Find.in(items).where(s -> s.length() < 5));
		isFalse(Find.in(items.stream()).where(s -> s.length() > 10));
	}

	@Test
	public void testFindOpsOnMap() {
		Map<Integer, String> nums = New.map(1, "one", 2, "two", 3, "three");

		Optional<Entry<Integer, String>> first = Find.firstOf(nums).where((k, v) -> k >= 2);
		eq(first.get(), 2, "two");

		Optional<Entry<Integer, String>> anyBig = Find.lastOf(nums).where((k, v) -> v.length() > 4);
		eq(anyBig.get(), 3, "three");

		Optional<Entry<Integer, String>> big = Find.anyOf(nums).where((k, v) -> v.length() == 5);
		eq(big.get(), 3, "three");

		Map<Integer, String> notSmall = Find.allOf(nums).where((k, v) -> k > 1);
		eq(notSmall, New.map(2, "two", 3, "three"));

		isTrue(Find.in(nums).where((k, v) -> k == 2));
		isFalse(Find.in(nums).where((k, v) -> k > 10));
	}

}
