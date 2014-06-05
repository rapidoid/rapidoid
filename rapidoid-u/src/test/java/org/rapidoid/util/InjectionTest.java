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

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.annotation.Resource;

import org.rapidoid.test.TestCommons;
import org.testng.annotations.Test;

class MyCallable implements Callable<String> {

	@Resource
	Foo foo;

	@Override
	public String call() throws Exception {
		return "abc";
	}
}

class Foo {
	@Resource
	Callable<String> callable;
}

class Table {

	@Resource
	Database db;

	@Resource
	Logger logger;

	@Resource
	Transactor transactor;
}

class Rel {
	@Resource
	Database db;
}

class Logger {
}

class Transactor {
}

class Store {
}

class Database {

	final Map<String, Table> tables = U.autoExpandingMap(Table.class);

	final Map<String, Rel> relations = U.autoExpandingMap(Rel.class);

}

class BookDao {

	@Resource
	Object em;

	@Resource
	Logger logger;
}

class PersonService {

}

class BookService {
	@Resource
	BookDao dao;

	@Resource
	Logger logger;
}

@Resource
class App {

	@Resource
	Logger logger;

	@Resource
	PersonService personService;

	@Resource
	BookService bookService;

	@Resource
	List<String> strings;
}

public class InjectionTest extends TestCommons {

	@Test
	public void shouldProvideInstance1() throws Exception {
		Database db1 = U.inject(Database.class);
		// db1 (tables1(a->tblA), relations(X->relX), transactor1, logger,
		// store1)

		Database db2 = U.inject(Database.class);
		// db1 (tables1(a->tblA), relations(X->relX), transactor1, logger,
		// store1)

		notNull(db1.tables.get("person"));
		notNull(db2.tables.get("person"));
	}

	@Test
	public void shouldProvideInstance() throws Exception {
		U.manage(MyCallable.class);

		Foo foo = U.wire(Foo.class);

		notNullAll(foo, foo.callable);
		hasType(foo.callable, MyCallable.class);

		MyCallable myCallable = (MyCallable) foo.callable;
		notNull(myCallable.foo);

		eq(myCallable.foo, foo);

		eq(foo.callable.call(), "abc");
	}

	@Test
	public void shouldCreateInstance() {
		U.manage(MyCallable.class);

		Bar bar = U.wire(Bar.class);

		notNullAll(bar, bar.callable);
	}

}
