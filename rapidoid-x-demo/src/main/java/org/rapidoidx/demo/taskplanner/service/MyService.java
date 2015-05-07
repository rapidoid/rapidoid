package org.rapidoidx.demo.taskplanner.service;

/*
 * #%L
 * rapidoid-x-demo
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HttpExchange;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class MyService {

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

	// e.g. /redir/ping
	public Object redir(String to, HttpExchange x) {
		return x.redirect("/" + to);
	}

}
