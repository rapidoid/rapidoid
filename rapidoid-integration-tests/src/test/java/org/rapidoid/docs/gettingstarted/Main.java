package org.rapidoid.docs.gettingstarted;

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

import org.rapidoid.annotation.Valid;
import org.rapidoid.jpa.JPA;
import org.rapidoid.setup.App;
import org.rapidoid.setup.On;

public class Main {

	public static void main(String[] args) {
		App.bootstrap(args).jpa(); // bootstrap JPA

		On.get("/books").json(() -> JPA.of(Book.class).all()); // get all books

		On.post("/books").json((@Valid Book b) -> JPA.save(b)); // insert new book if valid
	}

}

