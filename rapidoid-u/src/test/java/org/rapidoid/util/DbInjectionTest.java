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

import java.util.Map;

import org.rapidoid.test.TestCommons;
import org.testng.annotations.Test;

class Table {

	@Inject
	Database db;

	@Inject
	Logger logger;

	@Inject
	Transactor transactor;
}

class Rel {
	@Inject
	Database db;
}

class Logger {
}

class Transactor {
}

class Database {

	@Inject
	Transactor transactor;

	final Map<String, Table> tables = U.autoExpandingMap(Table.class);

	final Map<String, Rel> relations = U.autoExpandingMap(Rel.class);

}

public class DbInjectionTest extends TestCommons {

	@Test
	public void shouldInject() throws Exception {
		Database db = U.singleton(Database.class);
		isTrue(db == U.singleton(Database.class));
		isTrue(db == U.singleton(Database.class));

		notNull(db.tables);

		Table persons = db.tables.get("person");
		Table books = db.tables.get("book");

		notNullAll(persons, books);

		isTrue(persons != books);

		isTrue(persons.logger == books.logger);

		isTrue(persons.transactor == books.transactor);
		isTrue(persons.transactor == db.transactor);
	}

}
