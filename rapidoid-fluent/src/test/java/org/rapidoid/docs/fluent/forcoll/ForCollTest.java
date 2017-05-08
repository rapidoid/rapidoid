package org.rapidoid.docs.fluent.forcoll;

import org.junit.Test;
import org.rapidoid.fluent.For;
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

public class ForCollTest extends TestCommons {

	@Test
	@Doc(title = "Iterating through lists or sets")
	public void docs() {

		List<String> items = __(New.list("a", "bbb", "cc"));

		List<String> dest = New.list();

		For.each(items).where(s -> s.length() < 3).run(dest::add);

		System.out.println(__(dest));
	}

}
