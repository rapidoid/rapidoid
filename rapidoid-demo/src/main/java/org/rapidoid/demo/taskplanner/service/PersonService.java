package org.rapidoid.demo.taskplanner.service;

/*
 * #%L
 * rapidoid-demo
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

import java.util.List;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.dao.DAO;
import org.rapidoid.demo.taskplanner.model.Person;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.log.Log;
import org.rapidoid.plugins.DB;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class PersonService extends DAO<Person> {

	public List<Person> findByName(String search) {
		final String s = search.toLowerCase();
		return DB.find(Person.class, new Predicate<Person>() {
			@Override
			public boolean eval(Person p) {
				return p.name.toLowerCase().contains(s);
			}
		}, null);
	}

	// e.g. /hello
	public String hello() {
		return "Hello from PersonService";
	}

	// e.g. /person/add?name=nikolche&age=30
	public List<Person> add(Person p) {
		Log.info("Inserting person", "person", p);
		insert(p);
		return all();
	}

	// e.g. /params?x=1&y=2
	public Map<String, Object> params(Map<String, Object> params) {
		return params;
	}

}
