package org.rapidoid.docs.fluent.mix;

import org.junit.Test;
import org.rapidoid.fluent.Do;
import org.rapidoid.fluent.Find;
import org.rapidoid.fluent.Flow;
import org.rapidoid.fluent.New;
import org.rapidoid.test.Doc;
import org.rapidoid.test.TestCommons;

import java.util.List;
import java.util.Map;

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

public class FluentMixTest extends TestCommons {

	@Test
	@Doc(title = "Manipulating collections")
	public void docs() {

		/* Manipulating lists or sets */

		List<String> items = __(New.list("a", "bbbbb", "cc"));

		__(Find.allOf(items).where(s -> s.length() > 1));

		__(Do.map(items).to(s -> s.length()));

		__(Do.reduce(items).by((a, b) -> a + ":" + b).orElse(""));

		__(Do.group(items).by(s -> s.length()));

		/* Manipulating maps */

		Map<Integer, String> nums = __(New.map(1, "one", 2, "two", 3, "three"));

		__(Do.map(nums).to((k, v) -> k * 1000, (k, v) -> v.toUpperCase()));

		__(Do.group(nums).by((k, v) -> k % 2 == 0 ? "even" : "odd"));

		/* interface Flow extends Stream { with extras } */
		
		__(Flow.of("Hi", "there", "X").map(c -> c + "!").filter(s -> s.length() <= 3).toList());
	}

}
