package org.rapidoid.db;

/*
 * #%L
 * rapidoid-db-tests
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.db.model.Person;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.util.U;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class DbClassCRUDTest extends DbTestCommons {

	@Test
	public void testCRUD() {
		testDb(DB.db());
		testDb(DBs.instance("db1"));
		testDb(DBs.instance("db2"));
		testDb(DBs.instance("db3"));
	}

	private void testDb(Database db) {
		long id1 = db.insert(new Person("abc", 10));
		long id2 = db.insert(new Person("f", 20));
		long id3 = db.insert(new Person("xy", 30));

		// serialize the db
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		db.saveTo(out);
		byte[] bytes = out.toByteArray();

		db.shutdown();

		U.show(new String(bytes));

		db = DBs.instance(db.name() + "-new");

		// load the db (several times shouldn't matter)
		db.load(new ByteArrayInputStream(bytes));
		db.load(new ByteArrayInputStream(bytes));
		db.load(new ByteArrayInputStream(bytes));

		Person p1 = db.get(id1);
		Person p2 = db.get(id2, Person.class);
		Person p3 = db.get(id3);

		eq(p1.id, id1);
		eq(p1.name, "abc");
		eq(p1.age, 10);

		eq(p2.id, id2);
		eq(p2.name, "f");
		eq(p2.age, 20);

		eq(p3.id, id3);
		eq(p3.name, "xy");
		eq(p3.age, 30);

		Predicate<Person> pr = new Predicate<Person>() {
			@Override
			public boolean eval(Person p) throws Exception {
				return p.age > 18;
			}
		};

		List<Person> adults = db.find(pr);

		eq(adults.size(), 2);
		eq(adults.get(0).id, id2);
		eq(adults.get(1).id, id3);

		db.shutdown();
	}

}
