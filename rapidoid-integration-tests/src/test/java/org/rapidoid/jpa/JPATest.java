package org.rapidoid.jpa;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.fluent.Do;
import org.rapidoid.http.HttpTestCommons;
import org.rapidoid.job.Jobs;
import org.rapidoid.u.U;

import java.util.List;

/*
 * #%L
 * rapidoid-integration-tests
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class JPATest extends HttpTestCommons {

	@Test
	public void testBasicCRUD() {
		String[] path = {JPATest.class.getPackage().getName()};
		JPA.bootstrap(path);

		Jobs.executeAndWait(() -> JPA.transaction(() -> {
			Book b1 = new Book("book 1");
			Book b2 = new Book("book 2");
			Movie m1 = new Movie("movie 1");

			JPA.insert(b1);
			JPA.insert(b2);
			JPA.insert(m1);
			JPA.em().flush(); // not actually required
		}));

		Jobs.executeAndWait(() -> JPA.transaction(() -> {
			eq(JPA.getAllEntities().size(), 3);

			List<Book> books = JPA.getAll(Book.class);
			eq(Do.map(books).to(Book::getTitle), U.list("book 1", "book 2"));

			List<Movie> movies = JPA.getAll(Movie.class);
			eq(Do.map(movies).to(Movie::getTitle), U.list("movie 1"));

			eq(JPA.jpql("select title from Book where id = ?1", 2L), U.list("book 2"));
		}));

		eq(Jobs.errorCounter().get(), 0);
	}

}
