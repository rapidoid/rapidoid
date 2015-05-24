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
import org.rapidoid.demo.taskplanner.model.User;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.log.Log;
import org.rapidoid.plugins.DB;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class UserService extends DAO<User> {

	public List<User> findByName(String search) {
		final String s = search.toLowerCase();
		return DB.find(User.class, new Predicate<User>() {
			@Override
			public boolean eval(User u) {
				return u.name.toLowerCase().contains(s);
			}
		}, null);
	}

	// e.g. /hello
	public String hello() {
		return "Hello from UserService";
	}

	// e.g. /user/add?username=niko&name=nikolche&age=31
	public List<User> add(User u) {
		Log.info("Inserting user", "user", u);
		insert(u);
		return all();
	}

	// e.g. /params?x=1&y=2
	public Map<String, Object> params(Map<String, Object> params) {
		return params;
	}

}
