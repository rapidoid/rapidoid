package org.rapidoid.jpa;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.goodies.X;
import org.rapidoid.http.IsolatedIntegrationTest;
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
public class XTest extends IsolatedIntegrationTest {

	@Test
	public void testXQueries() {
		JPA.bootstrap(path());
		X.scaffold(Book.class);

		getReq("/books?a");

		postData("/books?a", U.map("title", "foo"));
		postData("/books?b", U.map("title", "bar"));

		getReq("/books/1");
		getReq("/books?b");

		putData("/books/1", U.map("title", "abc"));
		putData("/books/100", U.map("title", "zzzz"));
		deleteReq("/books/200");

		getReq("/books/1?b");
		getReq("/books?c");

		deleteReq("/books/1");

		getReq("/books/1?c");
		getReq("/books?d");
	}

}
