package org.rapidoid.sql.test;

/*
 * #%L
 * rapidoid-sql
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.sql.SQL;
import org.rapidoid.sql.test.Movie;
import org.rapidoid.sql.test.SQLTestCommons;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.List;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("6.0.0")
public class SQLAPITest extends SQLTestCommons {

	@Test
	public void testSQLAPI() {
		//SQL sql = new SQL(JDBC.newApi().hsql("test"));

		SQL.dropTableIfExists("movie");
		SQL.execute("CREATE TABLE movie (id int, title varchar(99))");

		SQL.insert("movie").values(10, "Rambo");
		SQL.insert("movie").values(20, "Hackers");

		for (int i = 0; i < 1000; i++) {
			SQL.insert("movie").values(100 + i, "movie" + i);
		}

		SQL.update("movie")
			.set("id", "123")
			.set("title", "Rambo 2")
			.where("title", "Rambo 2")
			.and("title", "Rambo 2")
			.execute();

		List<Map<String, Object>> rows = SQL.select("id", "title").from("movie").where("id < ?", 25).asList();

		eq(rows.size(), 2);
		eq(Msc.lowercase(rows.get(0)), U.map("id", 10, "title", "rambo"));
		eq(Msc.lowercase(rows.get(1)), U.map("id", 20, "title", "hackers"));

		List<Movie> movies = SQL.from("movie")
			.where("id < ?", 25)
			.and("? > id or id = ?", 50, 1000)
			.and("id = id")
			.as(Movie.class).all();

		eq(movies.size(), 2);

		Movie m1 = SQL.get(Movie.class, 10);
		eq(m1.id, 10);
		eq(m1.getTitle(), "Rambo");

		Movie movie1 = movies.get(0);
		eq(movie1.id, 10);
		eq(movie1.getTitle(), "Rambo");

		Movie movie2 = movies.get(1);
		eq(movie2.id, 20);
		eq(movie2.getTitle(), "Hackers");

		SQL.delete("movie")
			.where("id < ?", 25)
			.where("? > id or id = ?", 50, 1000)
			.execute();
	}

}
