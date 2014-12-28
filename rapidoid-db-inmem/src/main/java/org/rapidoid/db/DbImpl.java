package org.rapidoid.db;

/*
 * #%L
 * rapidoid-db-inmem
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.rapidoid.activity.NamedActivity;
import org.rapidoid.db.impl.DefaultDbList;
import org.rapidoid.db.impl.DefaultDbRef;
import org.rapidoid.db.impl.DefaultDbSet;
import org.rapidoid.inmem.InMem;
import org.rapidoid.lambda.Callback;
import org.rapidoid.lambda.Operation;
import org.rapidoid.lambda.Predicate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class DbImpl extends NamedActivity<Db> implements Db {

	private final String filename;
	private final InMem inmem;

	public DbImpl(String name, String filename) {
		super(name);

		this.filename = filename;
		this.inmem = new InMem(filename);

		initDbMapper();
	}

	@SuppressWarnings("rawtypes")
	private void initDbMapper() {
		SimpleModule dbModule = new SimpleModule("DbModule", new Version(1, 0, 0, null, null, null));

		dbModule.addDeserializer(DbList.class, new JsonDeserializer<DbList>() {
			@SuppressWarnings("unchecked")
			@Override
			public DbList deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
					JsonProcessingException {
				Map<String, Object> data = jp.readValueAs(Map.class);
				String relation = (String) data.get("relation");
				List<? extends Number> ids = (List<Number>) data.get("ids");
				return new DefaultDbList(DbImpl.this, relation, ids);
			}
		});

		dbModule.addDeserializer(DbSet.class, new JsonDeserializer<DbSet>() {
			@SuppressWarnings("unchecked")
			@Override
			public DbSet deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
					JsonProcessingException {
				Map<String, Object> data = jp.readValueAs(Map.class);
				String relation = (String) data.get("relation");
				List<? extends Number> ids = (List<Number>) data.get("ids");
				return new DefaultDbSet(DbImpl.this, relation, ids);
			}
		});

		dbModule.addDeserializer(DbRef.class, new JsonDeserializer<DbRef>() {
			@Override
			public DbRef deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
					JsonProcessingException {
				@SuppressWarnings("unchecked")
				Map<String, Object> data = jp.readValueAs(Map.class);
				String relation = (String) data.get("relation");
				Number id = (Number) data.get("id");
				return new DefaultDbRef(DbImpl.this, relation, id.longValue());
			}
		});

		inmem.getMapper().registerModule(dbModule);
	}

	@Override
	public long insert(Object record) {
		return inmem.insert(record);
	}

	@Override
	public void delete(long id) {
		inmem.delete(id);
	}

	@Override
	public <E> E get(long id) {
		return inmem.get(id);
	}

	@Override
	public <E> E getIfExists(long id) {
		return inmem.getIfExists(id);
	}

	@Override
	public <E> E get(long id, Class<E> clazz) {
		return inmem.get(id, clazz);
	}

	@Override
	public void update(long id, Object record) {
		inmem.update(id, record);
	}

	@Override
	public void update(Object record) {
		inmem.update(record);
	}

	@Override
	public long persist(Object record) {
		return inmem.persist(record);
	}

	@Override
	public <T> T read(long id, String column) {
		return inmem.read(id, column);
	}

	@Override
	public <E> List<E> getAll(Class<E> clazz) {
		return inmem.getAll(clazz);
	}

	@Override
	public <E> List<E> getAll(Class<E> clazz, String orderBy) {
		return inmem.getAll(clazz, orderBy);
	}

	@Override
	public <E> List<E> getAll(long... ids) {
		return inmem.getAll(ids);
	}

	@Override
	public <E> List<E> getAll(Collection<Long> ids) {
		return inmem.getAll(ids);
	}

	@Override
	public <E> List<E> find(Predicate<E> match) {
		return inmem.find(match);
	}

	@Override
	public <E> List<E> find(String searchPhrase) {
		return inmem.find(searchPhrase);
	}

	@Override
	public <E> List<E> find(Class<E> clazz, String query, Object... args) {
		return inmem.find(clazz, query, args);
	}

	@Override
	public <E> void each(Operation<E> lambda) {
		inmem.each(lambda);
	}

	@Override
	public void transaction(Runnable transaction, boolean readOnly) {
		inmem.transaction(transaction, readOnly);
	}

	@Override
	public void transaction(Runnable transaction, boolean readOnly, Callback<Void> callback) {
		inmem.transaction(transaction, readOnly, callback);
	}

	@Override
	public void saveTo(OutputStream output) {
		inmem.saveTo(output);
	}

	@Override
	public void load(InputStream in) {
		inmem.loadFrom(in);
	}

	@Override
	public Db start() {
		inmem.start();
		return this;
	}

	@Override
	public Db halt() {
		inmem.halt();
		return this;
	}

	@Override
	public Db shutdown() {
		inmem.shutdown();
		return this;
	}

	@Override
	public boolean isActive() {
		return inmem.isActive();
	}

	@Override
	public void destroy() {
		inmem.destroy();
	}

	@Override
	public long size() {
		return inmem.size();
	}

	@Override
	public String toString() {
		return "DB:" + name + "(" + filename + ")";
	}

	@Override
	public long getIdOf(Object record) {
		return InMem.getIdOf(record, false);
	}

	@Override
	public <E> DbList<E> list(String relation) {
		return new DefaultDbList<E>(this, relation);
	}

	@Override
	public <E> DbSet<E> set(String relation) {
		return new DefaultDbSet<E>(this, relation);
	}

	@Override
	public <E> DbRef<E> ref(String relation) {
		return new DefaultDbRef<E>(this, relation);
	}

}
