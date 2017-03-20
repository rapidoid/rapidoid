package org.rapidoid.docs.fluent.teaser;

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

import org.junit.Test;
import org.rapidoid.test.Doc;
import org.rapidoid.test.TestCommons;

public class FluentTeaserTest extends TestCommons {

	@Test
	@Doc(title = "Nice Fluent API with Java 8 streams behind the scenes")
	public void docs() {

		/* Using Java 8? Now you can EASILY filter, map, group and search through collections: */

		/// Find.firstOf(items).where(s -> s.length() < 3);

		/// Find.allOf(nums).where((k, v) -> k > 1);

		/// For.each(books).withNonNull(Book::title).run(this::publish);

		/// Do.map(words).to(s -> s.length());
	
		/// Do.group(items).by(Item::getCategory);
		
		/// Do.reduce(words).by((a, b) -> a + ":" + b).orElse("");
		
		/// Do.map(nums).toList((k, v) -> k * 2);
	}

}
