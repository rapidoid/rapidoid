package org.rapidoid.english;

/*
 * #%L
 * rapidoid-commons
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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.English;
import org.rapidoid.test.AbstractCommonsTest;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class EnglishTest extends AbstractCommonsTest {

	@Test
	public void testPlural() {
		eq(English.plural("todo"), "todos");
		eq(English.plural("alumnus"), "alumni");
		eq(English.plural("book"), "books");
		eq(English.plural("hero"), "heroes");
		eq(English.plural("box"), "boxes");
		eq(English.plural("phrase"), "phrases");
		eq(English.plural("dish"), "dishes");
		eq(English.plural("toy"), "toys");
		eq(English.plural("sky"), "skies");

		eq(English.plural("TODO"), "TODOS");
		eq(English.plural("ALUMNUS"), "ALUMNI");
		eq(English.plural("BOOK"), "BOOKS");
		eq(English.plural("HERO"), "HEROES");
		eq(English.plural("BOX"), "BOXES");
		eq(English.plural("PHRASE"), "PHRASES");
		eq(English.plural("DISH"), "DISHES");
		eq(English.plural("TOY"), "TOYS");
		eq(English.plural("SKY"), "SKIES");

		eq(English.plural("Todo"), "Todos");
	}

}
