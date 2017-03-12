package org.rapidoid.jpa;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.IsolatedIntegrationTest;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

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
public class HttpTransactionTest extends IsolatedIntegrationTest {

	@Test
	public void testWebTx() {
		JPA.bootstrap(path());

		On.get("/allBooks").json(() -> JPA.of(Book.class).all());
		On.post("/books").json((Book b) -> JPA.insert(b));

		On.post("/del").transaction().json((Long id) -> {
			JPA.delete(Book.class, id);
			JPA.flush(); // optional
			return U.list("DEL " + id, JPA.getAllEntities());
		});

		postData("/books?title=a", U.map("title", "My Book 1"));
		postData("/books?title=b", U.map("title", "My Book 2"));

		onlyGet("/allBooks");

		onlyPost("/del?id=1");
		onlyPost("/del?id=2");
	}

}
