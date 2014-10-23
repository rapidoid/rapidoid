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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.rapidoid.json.JSON;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.lambda.V1;
import org.rapidoid.util.U;

class Rec {
	final Class<?> type;
	final String json;

	public Rec(Class<?> type, String json) {
		this.type = type;
		this.json = json;
	}
}

public class DbImpl implements Db {

	private final AtomicLong ids = new AtomicLong();

	private final Map<Long, Rec> data = new ConcurrentHashMap<Long, Rec>(1000);

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	private static Rec rec(Object record, long id) {
		String _class = record.getClass().getCanonicalName();
		return new Rec(record.getClass(), JSON.stringifyWithExtras(record, "_class", _class, "id", id));
	}

	@SuppressWarnings("unchecked")
	private static <T> T obj(Rec rec) {
		return (T) JSON.parse(rec.json, rec.type);
	}

	@SuppressWarnings("unchecked")
	private static <T> T setId(T record, long id) {
		if (record != null) {

			if (record instanceof Map) {
				((Map<Object, Object>) record).put("id", id);
			}

			try {
				U.setId(record, id);
			} catch (Exception e) {
				// ignore
			}
		}

		return record;
	}

	@Override
	public long insert(Object record) {
		sharedLock();
		try {
			long id = ids.incrementAndGet();
			setId(record, id);
			data.put(id, rec(record, id));
			return id;
		} finally {
			sharedUnlock();
		}
	}

	@Override
	public void delete(long id) {
		sharedLock();
		try {
			validateId(id);
			data.remove(id);
		} finally {
			sharedUnlock();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> E get(long id) {
		sharedLock();
		try {
			validateId(id);
			Rec rec = data.get(id);
			return (E) (rec != null ? setId(obj(rec), id) : null);
		} finally {
			sharedUnlock();
		}
	}

	@Override
	public <E> E get(long id, Class<E> clazz) {
		sharedLock();
		try {
			validateId(id);
			return get_(id, clazz);
		} finally {
			sharedUnlock();
		}
	}

	@Override
	public void update(long id, Object record) {
		sharedLock();
		try {
			validateId(id);
			data.put(id, rec(record, id));
			setId(record, id);
		} finally {
			sharedUnlock();
		}
	}

	@Override
	public void update(Object record) {
		update(U.getId(record), record);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T read(long id, String column) {
		sharedLock();
		try {
			validateId(id);
			Map<String, Object> map = get_(id, Map.class);
			return (T) (map != null ? map.get(column) : null);
		} finally {
			sharedUnlock();
		}
	}

	@Override
	public <E> List<E> getAll(final Class<E> clazz) {
		return find(new Predicate<E>() {
			@Override
			public boolean eval(E record) throws Exception {
				return clazz.isAssignableFrom(record.getClass());
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> List<E> find(final Predicate<E> match) {
		final List<E> results = U.list();

		each(new V1<E>() {
			@Override
			public void execute(E record) throws Exception {
				if (match.eval(record)) {
					results.add(record);
				}
			}
		});

		return results;
	}

	@Override
	public <E> void each(final V1<E> lambda) {
		sharedLock();
		try {

			for (Entry<Long, Rec> entry : data.entrySet()) {
				E record = obj(entry.getValue());
				setId(record, entry.getKey());

				try {
					lambda.execute(record);
				} catch (ClassCastException e) {
					// ignore, cast exceptions are expected
				} catch (Exception e) {
					throw U.rte(e);
				}
			}
		} finally {
			sharedUnlock();
		}
	}

	@Override
	public void transaction(Runnable transaction) {
		globalLock();
		try {
			transaction.run();
		} finally {
			globalUnlock();
		}
	}

	private <T> T get_(long id, Class<T> clazz) {
		validateId(id);
		Rec rec = data.get(id);
		return rec != null ? setId(JSON.parse(rec.json, clazz), id) : null;
	}

	private void sharedLock() {
		lock.readLock().lock();
	}

	private void sharedUnlock() {
		lock.readLock().unlock();
	}

	private void globalLock() {
		lock.writeLock().lock();
	}

	private void globalUnlock() {
		lock.writeLock().unlock();
	}

	private void validateId(long id) {
		U.must(data.containsKey(id), "Cannot find DB record with id=%s", id);
	}

	@Override
	public void save(final OutputStream output) {
		globalLock();

		try {
			PrintWriter out = new PrintWriter(output);

			for (Entry<Long, Rec> entry : data.entrySet()) {
				String json = entry.getValue().json;
				out.println(json);
			}

			out.close();
		} finally {
			globalUnlock();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void load(InputStream in) {
		globalLock();

		try {
			data.clear();

			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String line;
			while ((line = reader.readLine()) != null) {
				Map<String, Object> map = JSON.parse(line, Map.class);
				Long id = new Long(((String) map.get("id")));
				String className = ((String) map.get("_class"));
				Class<?> type = U.getClassIfExists(className);
				data.put(id, new Rec(type, line));
			}

			data = new ConcurrentHashMap<Long, Rec>(data);

		} catch (IOException e) {
			throw U.rte("Cannot load database!", e);
		} finally {
			globalUnlock();
		}
	}

	}

}
