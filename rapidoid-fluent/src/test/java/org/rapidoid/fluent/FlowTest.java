package org.rapidoid.fluent;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Test;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;

/*
 * #%L
 * rapidoid-fluent
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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
public class FlowTest extends TestCommons {

	@Test
	public void testFlowOps() {
		List<String> items = U.list("a", "bbbbb", "cc");

		Stream<Integer> lengths1 = Flow.of(items).map(s -> s.length()).stream();
		eq(lengths1.collect(To.list()), U.list(1, 5, 2));

		List<Integer> lengths2 = Flow.of(items).map(s -> s.length()).toList();
		eq(lengths2, U.list(1, 5, 2));

		Set<Integer> lengths3 = Flow.of(items).map(s -> s.length()).toSet();
		eq(lengths3, U.set(1, 5, 2));

		Map<Integer, List<String>> byLengths = Flow.of(items).groupBy(s -> s.length());
		eq(byLengths, U.map(1, U.list("a"), 5, U.list("bbbbb"), 2, U.list("cc")));

		String joined = Flow.of(items).reverse().reduce((a, b) -> a + ":" + b).orElse("");
		eq(joined, "cc:bbbbb:a");

		String joined2 = Flow.of(items).reduce("@", (a, b) -> a + ":" + b);
		eq(joined2, "@:a:bbbbb:cc");

		List<String> lst = Flow.of("a", "", "bb", "ccc").filter(s -> !s.isEmpty()).map(String::toUpperCase).reverse()
				.toList();
		eq(lst, U.list("CCC", "BB", "A"));
	}

}
