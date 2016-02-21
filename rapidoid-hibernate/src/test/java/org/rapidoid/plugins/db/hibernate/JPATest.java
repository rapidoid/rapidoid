package org.rapidoid.plugins.JPA.hibernate;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.plugins.db.hibernate.Book;
import org.rapidoid.plugins.db.hibernate.JPA;
import org.rapidoid.plugins.db.hibernate.Movie;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;
import org.rapidoid.util.SimplePersistorProvider;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

/*
 * #%L
 * rapidoid-hibernate
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
@Since("3.0.0")
public class JPATest extends TestCommons {

	@Test
	public void testBasicCRUD() {

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("test-pu");
		EntityManager em = emf.createEntityManager();

		Ctxs.setPersisterProvider(new SimplePersistorProvider(em));
		Ctxs.open("test");

		final Book b1 = new Book("book 1");
		final Book b2 = new Book("book 2");
		final Movie m1 = new Movie("movie 1");

		JPA.transaction(new Runnable() {
			@Override
			public void run() {
				JPA.insert(b1);
				JPA.insert(b2);
				JPA.insert(m1);
			}
		}, false);

		eq(JPA.getAll().size(), 3);

		List<Object> all = JPA.getAll();
		System.out.println(all);

		eq(all.size(), 3);
		eq(U.set(all), U.set(b1, b2, m1));

		em.close();
		emf.close();
		Ctxs.close();
	}

}
