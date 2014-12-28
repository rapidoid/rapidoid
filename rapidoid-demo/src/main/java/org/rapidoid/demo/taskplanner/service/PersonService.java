package org.rapidoid.demo.taskplanner.service;

/*
 * #%L
 * rapidoid-demo
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

import org.rapidoid.db.CRUD;
import org.rapidoid.db.DB;
import org.rapidoid.demo.db.Person;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.util.U;

public class PersonService extends CRUD<Person> {

	public List<Person> olderThan(final int age) {
		return DB.find(new Predicate<Person>() {
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
	public List<Person> add(Person p) {
		U.info("Inserting person", "person", p);
		insert(p);
		return getAll();
	}

	// e.g. /params?x=1&y=2
	public Map<String, Object> params(Map<String, Object> params) {
		return params;
	}

}
