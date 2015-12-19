package org.rapidoid.quick;

/*
 * #%L
 * rapidoid-quick
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

import java.util.List;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.app.IOTool;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.http.HttpClient;
import org.rapidoid.http.RESTClient;
import org.rapidoid.http.Req;
import org.rapidoid.plugins.cache.CachePlugin;
import org.rapidoid.plugins.db.DBPlugin;
import org.rapidoid.plugins.email.EmailPlugin;
import org.rapidoid.plugins.entities.EntitiesPlugin;
import org.rapidoid.plugins.sms.SMSPlugin;
import org.rapidoid.plugins.templates.TemplatesPlugin;
import org.rapidoid.sql.SQLAPI;

import redis.clients.jedis.Jedis;

@Authors("Nikolche Mihajlovski")
@Since("4.2.0")
public interface Tools {

	List<Map<String, Object>> sql(String sql, Object... args);

	List<Map<String, Object>> cql(String cql, Object... args);

	void cql(String cql, Callback<List<Map<String, Object>>> callback, Object... args);

	Req req();

	void result(Object result);

	HttpClient http();

	RESTClient services();

	DBPlugin db();

	DBPlugin hibernate();

	DBPlugin cassandra();

	EntitiesPlugin entities();

	EmailPlugin email();

	SMSPlugin sms();

	CachePlugin cache();

	CachePlugin memcached();

	TemplatesPlugin templates();

	SQLAPI mysql();

	IOTool io();

	SQLAPI jdbc();

	Jedis redis();

}
