package org.rapidoid.util;

/*
 * #%L
 * rapidoid-u
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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.rapidoid.test.TestCommons;
import org.testng.annotations.Test;

class BookDao {
	@Resource
	Logger logger;
}

interface PersonService {
}

class PersonServiceImpl implements PersonService {
	@Resource
	Logger logger;
}

class BookService {
	@Resource
	BookDao dao;

	@Resource
	Logger logger;
}

@Resource
class App {

	static boolean READY = false;

	@Resource
	Logger logger;

	@Resource
	PersonService personService;

	@Resource
	PersonServiceImpl personService2;

	@Resource
	BookService bookService;

	@PostConstruct
	public void callThisWhenReady() {
		READY = true;
	}

}

public class AppInjectionTest extends TestCommons {

	@Test
	public void shouldInjectAndCallPostConstruct() {
		U.manage(App.class, PersonServiceImpl.class);
		isTrue(App.READY);

		App app = U.inject(App.class);
		same(app, U.inject(App.class), U.inject(App.class));

		notNull(app.personService);
		notNull(app.bookService);
		notNull(app.bookService.dao);

		same(app.personService, app.personService2);

		same(app.logger, app.personService2.logger, app.bookService.logger, app.bookService.dao.logger);
	}

}
