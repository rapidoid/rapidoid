package org.rapidoid.plugins.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.AppExchange;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.entity.Book;
import org.rapidoid.entity.Movie;
import org.rapidoid.jpa.dbplugin.JPADBPlugin;
import org.rapidoid.test.TestCommons;
import org.rapidoid.util.SimplePersistorFactory;
import org.rapidoid.util.U;
import org.junit.Test;

/*
 * #%L
 * rapidoid-jpa
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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
public class JPADBPluginTest extends TestCommons {

	@Test
	public void testBasicCRUD() {

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("test-pu");
		EntityManager em = emf.createEntityManager();

		Ctx.setPersistorFactory(new SimplePersistorFactory(em));

		AppExchange x = mock(AppExchange.class);
		returns(x.persistor(), em);

		Ctx.setExchange(x);

		final JPADBPlugin db = new JPADBPlugin();

		final Book b1 = new Book("book 1");
		final Book b2 = new Book("book 2");
		final Movie m1 = new Movie("movie 1");

		db.transaction(new Runnable() {
			@Override
			public void run() {
				db.insert(b1);
				db.insert(b2);
				db.insert(m1);
			}
		}, false);

		eq(db.size(), 3);

		List<Object> all = db.getAll();
		System.out.println(all);

		eq(all.size(), 3);
		eq(U.set(all), U.set(b1, b2, m1));

		em.close();
		emf.close();
	}

}
