package org.rapidoid.jpa;

import org.junit.Test;
import org.rapidoid.annotation.*;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.http.IsolatedIntegrationTest;
import org.rapidoid.http.Req;
import org.rapidoid.ioc.IoCContext;
import org.rapidoid.ioc.impl.IoCContextWrapper;
import org.rapidoid.ioc.Wired;
import org.rapidoid.jpa.impl.SharedContextAwareEntityManagerProxy;
import org.rapidoid.jpa.impl.SharedEntityManagerFactoryProxy;
import org.rapidoid.setup.App;
import org.rapidoid.u.U;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

/*
 * #%L
 * rapidoid-integration-tests
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class JPAInjectionTest extends IsolatedIntegrationTest {

	@Test
	public void testJPAInjection() {
		JPA.bootstrap(path());
		App.path(path());
		App.scan();

		postData("/books?title=a", U.map("title", "My Book 1"));
		postData("/books?title=b", U.map("title", "My Book 2"));
		postData("/books?title=c", U.map("title", "My Book 3"));

		onlyGet("/allBooks");

		onlyGet("/del?id=1");
		getAndPost("/del2?id=2");
		onlyPost("/del3?id=3");
		onlyPost("/del4?id=3");

		onlyGet("/allBooks?finally");
	}

}

@Controller
class MyCtrl {

	@Wired
	private IoCContext ioc;

	@javax.inject.Inject
	private EntityManager em;

	@PersistenceContext
	private EntityManager em2;

	@Wired
	private EntityManagerFactory emf;

	@Wired
	private JPATool jpa;

	@GET
	public Object allBooks() {
		checkInjected();
		return JPA.of(Book.class).all();
	}

	@POST("/books")
	@Transaction
	public Object insertBook(Book b) {
		checkInjected();
		return jpa.insert(b);
	}

	@GET
	@Transaction(TransactionMode.READ_WRITE)
	public Object del(long id) {
		checkInjected();

		U.must(!em.getTransaction().getRollbackOnly());

		jpa.delete(jpa.get(Book.class, id));
		em.flush(); // optional

		return U.list("DEL #" + id, JPA.getAllEntities().size() + " remaining");
	}

	@Page(raw = true)
	@Transaction
	public Object del2(long id, Req req) {
		checkInjected();

		U.must(em.getTransaction().getRollbackOnly() == HttpUtils.isGetReq(req));

		JPA.delete(Book.class, id);

		return U.list("DEL #" + id, JPA.getAllEntities().size() + " remaining");
	}

	@POST
	@Transaction(TransactionMode.READ_ONLY)
	public Object del3(long id) {
		checkInjected();

		U.must(em.getTransaction().getRollbackOnly());

		em.remove(em.find(Book.class, id));
		em.flush();

		return U.list("DEL #" + id, JPA.getAllEntities().size() + " remaining");
	}

	@POST
	@Transaction(TransactionMode.READ_ONLY)
	public Object del4(long id) {
		checkInjected();

		U.must(jpa.em().getTransaction().getRollbackOnly());

		jpa.delete(Book.class, id); // throws R/O tx exception

		return null;
	}

	private void checkInjected() {
		U.notNull(emf, "emf");
		U.notNull(em, "em");
		U.notNull(em2, "em2");
		U.notNull(ioc, "ioc");
		U.notNull(jpa, "jpa");

		U.must(emf == SharedEntityManagerFactoryProxy.INSTANCE, "wrong emf");
		U.must(em == SharedContextAwareEntityManagerProxy.INSTANCE, "wrong em");
		U.must(em == em2, "different EMs!");
		U.must(jpa.em() == em, "different EMs!");

		U.must(ioc.singleton(MyCtrl.class) == this);
		U.must(ioc.singleton(EntityManager.class) == em);
		U.must(ioc.singleton(EntityManagerFactory.class) == emf);

		U.must(ioc.getClass().equals(IoCContextWrapper.class));
	}

}
