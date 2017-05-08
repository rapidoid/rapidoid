package org.rapidoid.docs.jpacrud;

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

import org.rapidoid.jpa.JPA;
import org.rapidoid.setup.App;
import org.rapidoid.setup.On;

import javax.validation.Valid;

public class Main {

	public static void main(String[] args) {
		App.bootstrap(args).jpa(); // bootstrap JPA

		On.get("/books").json(() -> JPA.of(Book.class).all());
		On.get("/books/{id}").json((Integer id) -> JPA.get(Book.class, id));

		On.post("/books").json((@Valid Book b) -> JPA.save(b));
		On.put("/books").json((@Valid Book b) -> JPA.update(b));
	}

}
