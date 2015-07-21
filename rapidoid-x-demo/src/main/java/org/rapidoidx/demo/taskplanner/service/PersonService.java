package org.rapidoidx.demo.taskplanner.service;

/*
 * #%L
 * rapidoid-x-demo
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

import java.util.List;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.dao.DAO;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.log.Log;
import org.rapidoidx.db.XDB;
import org.rapidoidx.demo.taskplanner.model.Person;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class PersonService extends DAO<Person> {

	public List<Person> olderThan(final int age) {
		return XDB.find(new Predicate<Person>() {
			@Override
			public boolean eval(Person p) {
				return p.age > age;
			}
		});
	}

	// e.g. /hello
	public String hello() {
		return "Hello from PersonService";
	}

	// e.g. /person/add?name=nikolche&age=30
	public Iterable<Person> add(Person p) {
		Log.info("Inserting person", "person", p);
		insert(p);
		return all();
	}

	// e.g. /params?x=1&y=2
	public Map<String, Object> params(Map<String, Object> params) {
		return params;
	}

}
