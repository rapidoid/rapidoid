package custom.rapidoidx.db;

/*
 * #%L
 * rapidoid-x-db-tests
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.util.D;
import org.rapidoidx.db.DBs;
import org.rapidoidx.db.Database;
import org.rapidoidx.db.XDB;
import org.junit.Test;

import custom.rapidoidx.db.model.Person;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DbClassCRUDTest extends DbTestCommons {

	@Test
	public void testCRUD() {
		testDb(XDB.db());
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

		D.print(new String(bytes));

		db = DBs.instance(db.name() + "-new");

		// load the db (several times shouldn't matter)
		db.load(new ByteArrayInputStream(bytes));
		db.load(new ByteArrayInputStream(bytes));
		db.load(new ByteArrayInputStream(bytes));

		Person p1 = db.get(id1);
		Person p2 = db.get(id2, Person.class);
		Person p3 = db.get(id3);

		eq(p1.id(), id1);
		eq(p1.name, "abc");
		eq(p1.age, 10);

		eq(p2.id(), id2);
		eq(p2.name, "f");
		eq(p2.age, 20);

		eq(p3.id(), id3);
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
		eq(adults.get(0).id(), id2);
		eq(adults.get(1).id(), id3);

		db.shutdown();
	}

}
