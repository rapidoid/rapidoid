package org.rapidoid.docs.fluent.flow;

import org.junit.Test;
import org.rapidoid.fluent.Flow;
import org.rapidoid.test.Doc;
import org.rapidoid.test.TestCommons;

import java.util.List;
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

public class FluentFlowTest extends TestCommons {

	@Test
	@Doc(title = "interface Flow extends Stream { with extra methods for convenience }")
	public void docs() {

		/* Flow == Java 8 Stream++ */

		Set<Character> chars = __(Flow.chars('a', 'e').toSet());

		List<Long> nums = __(Flow.count(1, 7).toList());

		List<Long> range = __(Flow.range(1, 7).toList());

		__(Flow.chars('a', 'e').map(c -> c + "!").toList());

		__(Flow.chars('a', 'd').map(c -> c + "!").map(String::toUpperCase).toList());

		__(Flow.chars('a', 'c').map(c -> c + "!").filter(s -> !s.startsWith("b")).toList());

		__(Flow.of("Hi", "there", "X").map(c -> c + "!").filter(s -> s.length() <= 3).toList());
	}

}
