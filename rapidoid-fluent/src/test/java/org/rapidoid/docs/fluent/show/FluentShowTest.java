package org.rapidoid.docs.fluent.show;

import org.junit.Test;
import org.rapidoid.fluent.Find;
import org.rapidoid.fluent.New;
import org.rapidoid.test.Doc;
import org.rapidoid.test.TestCommons;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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

public class FluentShowTest extends TestCommons {

	@Test
	@Doc(title = "Why is Rapidoid Fluent so sweet?")
	public void docs() {

		Map<Integer, String> nums = __(New.map(1, "one", 2, "two", 3, "three"));

		/* Find the even numbers from the map - with Rapidoid Fluent (and Java 8 streams behind the scenes): */

		__(Find.allOf(nums).where((k, v) -> k % 2 == 0));

		/* That is equivalent to the following code: */

		Map<Integer, String> even = nums.entrySet().stream()
				.filter(num -> num.getKey() % 2 == 0)
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		System.out.println(__(even));

		/* Or, we can do the same thing in the old way */

		Map<Integer, String> even2 = new HashMap<>();

		for (Entry<Integer, String> num : nums.entrySet()) {
			int k = num.getKey();
			if (k % 2 == 0) {
				even2.put(k, num.getValue());
			}
		}

		System.out.println(__(even2));
	}

}
