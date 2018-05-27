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

package org.rapidoid.docs.jpascaffold;

import org.rapidoid.goodies.Boot;
import org.rapidoid.gui.GUI;
import org.rapidoid.jpa.JPA;
import org.rapidoid.setup.App;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

import java.util.List;

public class Main extends GUI {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		App.run(args, "users.admin.password=a"); // demo-only password

		Boot.jpa()
			.auth()
			.entities()
			.overview();

		App.gui()
			.search(true)
			.brand("Cool app");

		On.page("/").mvc("Welcome!");

		String search = "FROM Book b WHERE b.title LIKE ?1";
		On.page("/search").mvc((String q) -> {
			List<Book> records = JPA.jpql(search, "%" + q + "%").all();
			return U.list(h2("Searching for: ", q), grid(records));
		});
	}

}
