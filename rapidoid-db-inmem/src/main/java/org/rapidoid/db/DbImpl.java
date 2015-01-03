package org.rapidoid.db;

/*
 * #%L
 * rapidoid-db-inmem
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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.rapidoid.activity.NamedActivity;
import org.rapidoid.db.impl.DefaultDbList;
import org.rapidoid.db.impl.DefaultDbRef;
import org.rapidoid.db.impl.DefaultDbSet;
import org.rapidoid.inmem.InMem;
import org.rapidoid.lambda.Callback;
import org.rapidoid.lambda.Operation;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.util.U;

public class DbImpl extends NamedActivity<Db> implements Db {

	@SuppressWarnings("unchecked")
	private static final Set<Class<?>> REL_CLASSES = U.set(DbList.class, DbSet.class, DbRef.class);

	private final InMem inmem;

	public DbImpl(String name, String filename) {
		super(name);
		this.inmem = new InMem(filename, new JacksonEntitySerializer(this), REL_CLASSES);
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
	public long persistedIdOf(Object record) {
		return inmem.persistedIdOf(record);
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
		return "DB:" + name + "(" + inmem + ")";
	}

	@Override
	public long getIdOf(Object record) {
		return InMem.getIdOf(record, false);
	}

	@Override
	public <E> DbList<E> list(Object holder, String relation) {
		return new DefaultDbList<E>(this, holder, relation);
	}

	@Override
	public <E> DbSet<E> set(Object holder, String relation) {
		return new DefaultDbSet<E>(this, holder, relation);
	}

	@Override
	public <E> DbRef<E> ref(Object holder, String relation) {
		return new DefaultDbRef<E>(this, holder, relation);
	}

}
