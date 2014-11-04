package org.rapidoid.inmem;

/*
 * #%L
 * rapidoid-inmem
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.rapidoid.json.JSON;
import org.rapidoid.lambda.Operation;
import org.rapidoid.lambda.Predicate;

class Rec {
	final Class<?> type;
	final String json;

	public Rec(Class<?> type, String json) {
		this.type = type;
		this.json = json;
	}
}

public class InMem {

	private static final String SUFFIX_B = "b";

	private static final String SUFFIX_A = "a";

	private static final byte[] CR_LF = { 13, 10 };

	private final String filename;

	private final AtomicLong ids = new AtomicLong();

	private final AtomicBoolean active = new AtomicBoolean(true);

	private final AtomicBoolean aOrB = new AtomicBoolean(true);

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	private final Thread persistor;

	private ConcurrentHashMap<Long, Rec> prevData = new ConcurrentHashMap<Long, Rec>();

	private ConcurrentHashMap<Long, Rec> data = new ConcurrentHashMap<Long, Rec>();

	public InMem() {
		this(null);
	}

	public InMem(String filename) {
		this.filename = filename;

		if (filename != null && !filename.isEmpty()) {
			persistor = new Thread(new Runnable() {
				@Override
				public void run() {
					persist();
				}
			});
			persistor.start();
		} else {
			persistor = null;
		}
	}

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

	public <E> E get(long id, Class<E> clazz) {
		sharedLock();
		try {
			validateId(id);
			return get_(id, clazz);
		} finally {
			sharedUnlock();
		}
	}

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

	public void update(Object record) {
		update(getObjId(record), record);
	}

	@SuppressWarnings("unchecked")
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

	public <E> List<E> getAll(final Class<E> clazz) {
		return find(new Predicate<E>() {
			@Override
			public boolean eval(E record) throws Exception {
				return clazz.isAssignableFrom(record.getClass());
			}
		});
	}

	public <E> List<E> find(final Predicate<E> match) {
		final List<E> results = new ArrayList<E>();

		each(new Operation<E>() {

			@Override
			public void execute(E record) throws Exception {
				if (match.eval(record)) {
					results.add(record);
				}
			}
		});

		return results;
	}

	public <E> void each(final Operation<E> lambda) {
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
					throw new RuntimeException(e);
				}
			}
		} finally {
			sharedUnlock();
		}
	}

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
		must(data.containsKey(id), "Cannot find DB record with id=%s", id);
	}

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

				Class<?> type;
				try {
					type = Class.forName(className);
				} catch (ClassNotFoundException e) {
					type = null;
				}

				data.put(id, new Rec(type, line));
			}

			prevData = new ConcurrentHashMap<Long, Rec>(data);

		} catch (IOException e) {
			throw new RuntimeException("Cannot load database!", e);
		} finally {
			globalUnlock();
		}
	}

	private void persistTo(RandomAccessFile file) throws IOException {
		for (Entry<Long, Rec> entry : data.entrySet()) {
			String json = entry.getValue().json;
			file.write(json.getBytes());
			file.write(CR_LF);
		}
	}

	private void persistData(Runnable onCommit, Runnable onRollback) {
		globalLock();

		final ConcurrentHashMap<Long, Rec> copy;
		try {
			if (data.isEmpty()) {
				return;
			}
			copy = new ConcurrentHashMap<Long, Rec>(data);
		} finally {
			globalUnlock();
		}

		try {
			boolean isA = aOrB.get();
			String suffixAorB = isA ? SUFFIX_A : SUFFIX_B;
			File file = new File(filenameWithSuffix(suffixAorB));

			if (file.exists()) {
				file.delete();
			}

			RandomAccessFile ff = new RandomAccessFile(file, "rw");
			persistTo(ff);
			ff.getChannel().force(false);
			ff.close();

			prevData = copy;
			must(aOrB.compareAndSet(isA, !isA), "DB persistence filename switching error!");

			try {
				if (onCommit != null) {
					onCommit.run();
				}
			} catch (Throwable e) {
				error("Tx commit callback error", e);
				// ignore
			}

		} catch (IOException e) {
			try {
				if (onRollback != null) {
					onRollback.run();
				}
			} catch (Throwable e2) {
				error("Tx rollback callback error", e2);
				// ignore
			}

			data = new ConcurrentHashMap<Long, Rec>(prevData);

			throw new RuntimeException("Cannot persist database changes!", e);
		}
	}

	private String filenameWithSuffix(String suffixAorB) {
		return filename.replace(".db", "-" + suffixAorB + ".db");
	}

	private void persist() {
		while (!Thread.interrupted()) {
			try {
				persistData(null, null);
			} catch (Exception e1) {
				error("Failed to persist data!", e1);
			}

			if (active.get()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// FIXME return immediately if halt
				}
			} else {
				try {
					persistData(null, null);
				} catch (Exception e1) {
					error("Failed to persist data!", e1);
				}
				return;
			}
		}
	}

	public void shutdown() {
		active.set(false);
		try {
			persistor.join();
		} catch (InterruptedException e) {
		}
	}

	public boolean isActive() {
		return active.get();
	}

	@Override
	public String toString() {
		return super.toString() + "[filename=" + filename + "]";
	}

	public void start() {
		// TODO implement start after shutdown
		if (!active.get()) {
			throw new IllegalStateException("Starting the database after shutdown is not implemented yet!");
		}
	}

	public void halt() {
		if (active.get()) {
			active.set(false);

			persistor.interrupt();
			try {
				persistor.join();
			} catch (InterruptedException e) {
			}
		}
	}

	public void destroy() {
		halt();
		new File(filename).delete();
		new File(filenameWithSuffix(SUFFIX_A)).delete();
		new File(filenameWithSuffix(SUFFIX_B)).delete();
	}

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
				setObjId(record, id);
			} catch (Exception e) {
				// ignore
			}
		}

		return record;
	}

	private static void must(boolean condition, String msg, Object... args) {
		if (!condition) {
			throw new RuntimeException(String.format(msg, args));
		}
	}

	private static void error(String msg, Throwable e) {
		System.err.println(msg);
		e.printStackTrace();
	}

	private static void setObjId(Object obj, long id) {
		Class<?> c = obj.getClass();

		try {
			try {
				c.getMethod("setId", long.class).invoke(obj, id);
				return;
			} catch (NoSuchMethodException e1) {
				try {
					c.getMethod("id", long.class).invoke(obj, id);
					return;
				} catch (NoSuchMethodException e2) {
					try {
						c.getField("id").set(obj, id);
						return;
					} catch (NoSuchFieldException e3) {
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot get object id!", e);
		}

		throw new RuntimeException(
				"Cannot find public 'id' field nor 'setId' setter method nor 'id' setter method in class: " + c);
	}

	private static long getObjId(Object obj) {
		Class<?> c = obj.getClass();

		try {
			try {
				return ((Number) c.getMethod("getId").invoke(obj)).longValue();
			} catch (NoSuchMethodException e1) {
				try {
					return ((Number) c.getMethod("id").invoke(obj)).longValue();
				} catch (NoSuchMethodException e2) {
					try {
						return ((Number) c.getField("id").get(obj)).longValue();
					} catch (NoSuchFieldException e3) {
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot get object id!", e);
		}

		throw new RuntimeException(
				"Cannot find public 'id' field nor 'getId' getter method nor 'id' getter method in class: " + c);
	}

	public int size() {
		globalLock();
		try {
			return data.size();
		} finally {
			globalUnlock();
		}
	}

}
