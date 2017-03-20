package org.rapidoid.docs.jpascaffold;

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

import org.rapidoid.goodies.X;
import org.rapidoid.gui.GUI;
import org.rapidoid.jpa.JPA;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

import java.util.List;

public class Main extends GUI {

	public static void main(String[] args) {

		On.page("/").mvc("Welcome!");
		X.scaffold(Book.class);

		String search = "FROM Book b WHERE b.title LIKE ?1";
		On.page("/search").mvc((String q) -> {
			List<Book> records = JPA.jpql(search, "%" + q + "%").all();
			return U.list(h2("Searching for: ", q), grid(records));
		});
	}

}
