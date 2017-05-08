package org.rapidoid.benchmark.highlevel;

/*
 * #%L
 * rapidoid-benchmark
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

import org.rapidoid.benchmark.common.Helper;
import org.rapidoid.benchmark.common.Message;
import org.rapidoid.config.Conf;
import org.rapidoid.env.Env;
import org.rapidoid.http.MediaType;
import org.rapidoid.jdbc.JDBC;
import org.rapidoid.jdbc.JdbcClient;
import org.rapidoid.log.Log;
import org.rapidoid.setup.App;
import org.rapidoid.setup.On;

public class Main {

	public static void main(String[] args) {
		App.run(args);

		Conf.HTTP.set("maxPipeline", 128);
		Conf.HTTP.set("timeout", 0);
		Conf.HTTP.sub("mandatoryHeaders").set("connection", false);

		On.port(8080);

		setupDbHandlers();
		setupSimpleHandlers();
	}

	private static void setupSimpleHandlers() {
		On.get("/plaintext").managed(false).contentType(MediaType.TEXT_PLAIN).serve("Hello, world!");
		On.get("/json").managed(false).json(() -> new Message("Hello, world!"));
	}

	private static void setupDbHandlers() {
		String dbHost = Conf.ROOT.entry("dbhost").or("localhost");
		Log.info("Database hostname is: " + dbHost);

		JdbcClient jdbc = JDBC.api();

		if (Env.hasProfile("mysql")) {
			jdbc.url("jdbc:mysql://" + dbHost + ":3306/hello_world?" + Helper.MYSQL_CONFIG);

		} else if (Env.hasProfile("postgres")) {
			jdbc.url("jdbc:postgresql://" + dbHost + ":5432/hello_world?" + Helper.POSTGRES_CONFIG);

		} else {
			jdbc.hsql("public");
			jdbc.execute("create table fortune (id int, message varchar(100))");
			jdbc.execute("insert into fortune (id, message) values (10, 'Hello')");
		}

		On.get("/fortunes").managed(false).html(new FortunesHandler(jdbc));
		On.get("/fortunes/multi").html(new FortunesMultiHandler(jdbc));
	}

}
