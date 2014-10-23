package org.rapidoid.demo.pojo;

import java.util.List;

import org.rapidoid.db.CRUD;
import org.rapidoid.db.DB;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.util.U;

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

public class PersonService extends CRUD<Person> {

	public PersonService() {
		super(Person.class);
	}

	public List<Person> olderThan(final int age) {
		return DB.find(new Predicate<Person>() {
			@Override
			public boolean eval(Person p) {
				return p.getAge() > age;
			}
		});
	}

	// e.g. /hello
	public String hello() {
		return "Hello from PersonService";
	}

	// e.g. /person/add?name=nick&age=30
	public List<Person> add(Person p) {
		U.info("Inserting person", "person", p);
		insert(p);
		return getAll();
	}

}
