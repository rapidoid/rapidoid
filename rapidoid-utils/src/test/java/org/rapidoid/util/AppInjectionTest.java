package org.rapidoid.util;

/*
 * #%L
 * rapidoid-utils
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import org.rapidoid.annotation.Autocreate;
import org.rapidoid.annotation.Init;
import org.rapidoid.annotation.Inject;
import org.rapidoid.test.TestCommons;
import org.testng.annotations.Test;

class BookDao {
	@Inject
	Logger logger;
}

interface PersonService {
}

class PersonServiceImpl implements PersonService {
	@Inject
	Logger logger;
}

class BookService {
	@Inject
	BookDao dao;

	@Inject
	Logger logger;
}

@Autocreate
class App {

	static boolean READY = false;

	@Inject
	Logger logger;

	@Inject
	PersonService personService;

	@Inject
	PersonServiceImpl personService2;

	@Inject
	BookService bookService;

	@Init
	public void callThisWhenReady() {
		READY = true;
	}

}

public class AppInjectionTest extends TestCommons {

	@Test
	public void shouldInjectAndCallPostConstruct() {
		UTILS.manage(App.class, PersonServiceImpl.class);
		isTrue(App.READY);

		App app = UTILS.singleton(App.class);
		same(app, UTILS.singleton(App.class), UTILS.singleton(App.class));

		notNull(app.personService);
		notNull(app.bookService);
		notNull(app.bookService.dao);

		same(app.personService, app.personService2);

		same(app.logger, app.personService2.logger, app.bookService.logger, app.bookService.dao.logger);
	}

}
