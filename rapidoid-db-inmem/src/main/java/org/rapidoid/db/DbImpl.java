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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.rapidoid.inmem.InMem;
import org.rapidoid.lambda.Operation;
import org.rapidoid.lambda.Predicate;

public class DbImpl implements Db {

	private final String name;
	private final String filename;
	private final InMem inmem;

	public DbImpl(String name, String filename) {
		this.name = name;
		this.filename = filename;
		this.inmem = new InMem(filename);
	}

	@Override
	public String name() {
		return name;
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
	public <T> T read(long id, String column) {
		return inmem.read(id, column);
	}

	public <E> List<E> getAll(Class<E> clazz) {
		return inmem.getAll(clazz);
	}

	@Override
	public <E> List<E> find(Predicate<E> match) {
		return inmem.find(match);
	}

	@Override
	public <E> void each(Operation<E> lambda) {
		inmem.each(lambda);
	}

	@Override
	public void transaction(Runnable transaction) {
		inmem.transaction(transaction);
	}

	@Override
	public void save(OutputStream output) {
		inmem.save(output);
	}

	@Override
	public void load(InputStream in) {
		inmem.load(in);
	}

	@Override
	public void shutdown() {
		inmem.shutdown();
	}

	@Override
	public boolean isActive() {
		return inmem.isActive();
	}

	@Override
	public String toString() {
		return "DB:" + name + "(" + filename + ")";
	}

	@Override
	public void halt() {
		inmem.halt();
	}

	@Override
	public void destroy() {
		inmem.destroy();
	}

	@Override
	public long size() {
		return inmem.size();
	}

}
