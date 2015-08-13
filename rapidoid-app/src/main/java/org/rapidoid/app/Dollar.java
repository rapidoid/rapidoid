package org.rapidoid.app;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HTTP;
import org.rapidoid.http.HttpClient;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.http.Services;
import org.rapidoid.http.ServicesClient;
import org.rapidoid.plugins.Plugins;
import org.rapidoid.plugins.cache.CachePlugin;
import org.rapidoid.plugins.db.DBPlugin;
import org.rapidoid.plugins.email.EmailPlugin;
import org.rapidoid.plugins.entities.EntitiesPlugin;
import org.rapidoid.plugins.languages.LanguagesPlugin;
import org.rapidoid.plugins.lifecycle.LifecyclePlugin;
import org.rapidoid.plugins.sms.SMSPlugin;
import org.rapidoid.plugins.templates.TemplatesPlugin;
import org.rapidoid.plugins.users.UsersPlugin;
import org.rapidoid.sql.SQL;
import org.rapidoid.sql.SQLAPI;
import org.rapidoid.util.U;

/*
 * #%L
 * rapidoid-app
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

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class Dollar {

	public final Map<?, ?> map = U.map();

	public final Map<Object, Map<Object, Object>> maps = U.mapOfMaps();

	public final Map<Object, List<Object>> lists = U.mapOfLists();

	public final Map<Object, Set<Object>> sets = U.mapOfSets();

	public final HttpClient http = HTTP.DEFAULT_CLIENT;

	public final ServicesClient services = Services.DEFAULT_CLIENT;

	public final LifecyclePlugin lifecycle = Plugins.lifecycle();

	public final LanguagesPlugin languages = Plugins.languages();

	public final DBPlugin db = Plugins.db();

	public final DBPlugin hibernate = Plugins.db("hibernate");

	public final DBPlugin cassandra = Plugins.db("cassandra");

	public final EntitiesPlugin entities = Plugins.entities();

	public final UsersPlugin users = Plugins.users();

	public final EmailPlugin email = Plugins.email();

	public final SMSPlugin sms = Plugins.sms();

	public final CachePlugin cache = Plugins.cache();

	public final TemplatesPlugin templates = Plugins.templates();

	public final SQLAPI jdbc;

	public final Map<String, Object> bindings;

	private final HttpExchange exchange;

	public Dollar(HttpExchange x, Map<String, Object> bindings) {
		this.exchange = x;
		this.bindings = bindings;
		this.jdbc = SQL.newInstance().mysql();
	}

	public List<Map<String, Object>> sql(String sql, Object... args) {
		if (sql.trim().toLowerCase().startsWith("select ")) {
			return jdbc.query(sql, args);
		} else {
			jdbc.execute(sql, args);
			return null;
		}
	}

	public Iterable<Map<String, Object>> cql(String cql, Object... args) {
		return cassandra.query(cql, args);
	}

	public HttpExchange req() {
		return exchange;
	}

	public DollarPage page(Object value, Map<String, Object> config) {
		return new DollarPage(value, config);
	}

}
