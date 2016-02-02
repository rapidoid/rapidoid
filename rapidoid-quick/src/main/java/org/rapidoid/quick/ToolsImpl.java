package org.rapidoid.quick;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.app.IOTool;
import org.rapidoid.app.IOToolImpl;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.http.*;
import org.rapidoid.plugins.Plugins;
import org.rapidoid.plugins.cache.CachePlugin;
import org.rapidoid.plugins.db.DBPlugin;
import org.rapidoid.plugins.email.EmailPlugin;
import org.rapidoid.plugins.entities.EntitiesPlugin;
import org.rapidoid.plugins.sms.SMSPlugin;
import org.rapidoid.plugins.templates.TemplatesPlugin;
import org.rapidoid.sql.SQL;
import org.rapidoid.sql.SQLAPI;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;

/*
 * #%L
 * rapidoid-quick
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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
@Since("4.2.0")
public class ToolsImpl implements Tools {

	private static volatile ToolsImpl INSTANCE;

	private final HttpClient http = HTTP.DEFAULT_CLIENT;

	private final RESTClient services = REST.DEFAULT_CLIENT;

	private final DBPlugin db = Plugins.db();

	private final DBPlugin hibernate = Plugins.db("hibernate");

	private final DBPlugin cassandra = Plugins.db("cassandra");

	private final EntitiesPlugin entities = Plugins.entities();

	private final EmailPlugin email = Plugins.email();

	private final SMSPlugin sms = Plugins.sms();

	private final CachePlugin cache = Plugins.cache();

	private final CachePlugin memcached = Plugins.cache("memcached");

	private final TemplatesPlugin templates = Plugins.templates();

	private final SQLAPI mysql = SQL.defaultInstance();

	private final IOTool io = new IOToolImpl();

	private final SQLAPI jdbc = SQL.defaultInstance();

	public static ToolsImpl getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ToolsImpl();
		}

		return INSTANCE;
	}

	@Override
	public List<Map<String, Object>> sql(String sql, Object... args) {
		if (sql.trim().toLowerCase().startsWith("select ")) {
			return jdbc.query(sql, args);
		} else {
			jdbc.execute(sql, args);
			return null;
		}
	}

	@Override
	public List<Map<String, Object>> cql(String cql, Object... args) {
		return cassandra.query(cql, args);
	}

	@Override
	public void cql(String cql, Callback<List<Map<String, Object>>> callback, Object... args) {
		cassandra.queryAsync(cql, callback, args);
	}

	@Override
	public Req req() {
		return Ctxs.ctx().exchange();
	}

	@Override
	public void result(Object result) {
		req().response().content(result).done();
	}

	@Override
	public HttpClient http() {
		return http;
	}

	@Override
	public RESTClient services() {
		return services;
	}

	@Override
	public DBPlugin db() {
		return db;
	}

	@Override
	public DBPlugin hibernate() {
		return hibernate;
	}

	@Override
	public DBPlugin cassandra() {
		return cassandra;
	}

	@Override
	public EntitiesPlugin entities() {
		return entities;
	}

	@Override
	public EmailPlugin email() {
		return email;
	}

	@Override
	public SMSPlugin sms() {
		return sms;
	}

	@Override
	public CachePlugin cache() {
		return cache;
	}

	@Override
	public CachePlugin memcached() {
		return memcached;
	}

	@Override
	public TemplatesPlugin templates() {
		return templates;
	}

	@Override
	public SQLAPI mysql() {
		return mysql;
	}

	@Override
	public IOTool io() {
		return io;
	}

	@Override
	public SQLAPI jdbc() {
		return jdbc;
	}

	@Override
	public Jedis redis() {
		return JedisTool.get();
	}

}
