/*-
 * #%L
 * rapidoid-integration-tests
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
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

package org.rapidoid.jpa;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.IsolatedIntegrationTest;
import org.rapidoid.setup.On;

@Authors("Nikolche Mihajlovski")
@Since("5.5.1")
public class JPALazyEntitiesTest extends IsolatedIntegrationTest {

	@Test
	public void testLazyEntitiesWithoutTx() {
		JPA.bootstrap(path());
		initData();

		On.get("/movies").json(() -> JPA.of(Movie.class).all());

		onlyGet("/movies");
	}

	@Test
	public void testLazyEntitiesInTx() {
		JPA.bootstrap(path());
		initData();

		On.get("/movies").transaction().json(() -> JPA.of(Movie.class).all());

		onlyGet("/movies");
	}

	private void initData() {
		JPA.transaction(() -> {
			Movie movie = new Movie("Mr. Bean");

			Tag tag = new Tag("comedy");
			JPA.save(tag);

			movie.tags.add(tag);
			JPA.insert(movie);
		});
	}

}
