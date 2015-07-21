package org.rapidoid.plugins.db;

/*
 * #%L
 * rapidoid-plugins
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.test.TestCommons;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class DBTest extends TestCommons {

	/**
	 * A demo example of DB API usage.
	 */
	@SuppressWarnings("unused")
	@Test
	public void showDbAPI() throws IOException {
		Iterable<Book> books = DB.read(Book.class, "id1", "title", "year", "comments");

		DB.addToSet("user1", "likes", "movie1");
		DB.removeFromSet("user2", "likes", "book4");
		Set<String> liked = DB.getSetItems("user1", "likes");
		int size = DB.getSetSize("user1", "likes");

		DB.addToList("user2", "todos", "buy something");
		DB.removeFromList("user3", "todos", "go somewhere");
		List<String> todos = DB.getListItems("user1", "todos");
		int size2 = DB.getListSize("user1", "todos");
	}

}
