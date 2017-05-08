package org.rapidoid.docs.fluent.findcoll;

import org.junit.Test;
import org.rapidoid.fluent.Find;
import org.rapidoid.fluent.New;
import org.rapidoid.test.Doc;
import org.rapidoid.test.TestCommons;

import java.util.List;

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

public class FindCollTest extends TestCommons {

	@Test
	@Doc(title = "Searching through lists or sets")
	public void docs() {

		/* Searching through lists or sets: */

		List<String> items = __(New.list("a", "bbbbb", "cc"));

		__(Find.firstOf(items).where(s -> s.length() < 3));

		__(Find.lastOf(items).where(s -> s.length() > 4));

		__(Find.anyOf(items).where(s -> s.length() == 5));

		__(Find.allOf(items).where(s -> s.length() > 1));

		__(Find.in(items).where(s -> s.length() < 5));

		__(Find.in(items.stream()).where(s -> s.length() > 10));
	}

}
