package org.rapidoid.demo.pojo;

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

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainService {

	// e.g. /
	public String index() {
		return "Hello world!";
	}

	// e.g. /ping
	public String ping() {
		return "pong";
	}

	// e.g. /hey/joe/23
	public String hey(String name, byte age, boolean driver) {
		return "Hey " + name + " (" + age + "), driver: " + driver;
	}

	// e.g. /event/12/dec/2013
	public String event(String day, String month, String year) {
		return "Event at " + day + " " + month + " " + year;
	}

	// e.g. /date/31.12.2013 or 31-12-2013 or 2013-12-31 etc.
	public Date date(Date date) {
		return date;
	}

	// e.g. /params?x=1&y=2
	public Map<String, Object> params(Map<String, Object> params) {
		return params;
	}

	// e.g. /arr/aa/bb/c
	public String[] arr(String... params) {
		return params;
	}

	// e.g. /listOf/aa/bb/c
	public List<String> listOf(List<String> params) {
		return params;
	}

	// e.g. /setOf/a/b/c/a/b
	public Set<String> setOf(Set<String> params) {
		return params;
	}

	// e.g. /addPerson?name=nick&age=30
	public Person addPerson(Person p) {
		System.out.println("Inserting " + p);
		return p;
	}

	// e.g. /user?a=1
	public Object user(Principal user, Map<String, Object> params) {
		params.put("user", user);
		return params;
	}

}
