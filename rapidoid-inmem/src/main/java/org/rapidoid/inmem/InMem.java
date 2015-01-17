package org.rapidoid.inmem;

/*
 * #%L
 * rapidoid-inmem
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

import org.rapidoid.annotation.Relation;
import org.rapidoid.lambda.Callback;
import org.rapidoid.lambda.Operation;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.prop.Prop;
import org.rapidoid.prop.PropertyFilter;
import org.rapidoid.prop.PropertySelector;
import org.rapidoid.util.Cls;
import org.rapidoid.util.Tuple;
import org.rapidoid.util.U;

class Rec {
	final Class<?> type;
	final byte[] bytes;

	public Rec(Class<?> type, byte[] bytes) {
		this.type = type;
		this.bytes = bytes;
	}
}

/**
 * Simple, persisted in-memory NoSQL DB, based on {@link ConcurrentSkipListMap}.<br>
 * 
 * ACID transactional semantics:<br>
 * - Atomicity with automatic rollback in case of exception,<br>
 * - Consistency - only with constraints enforced programmatically inside transaction,<br>
 * - Isolation is serializable (with global lock),<br>
 * - Durability through on-commit callbacks.<br>
 */
public class InMem {

	private static final String META_UPTIME = "uptime";

	private static final String META_TIMESTAMP = "timestamp";

	private static final String SUFFIX_B = "b";

	private static final String SUFFIX_A = "a";

	private static final byte[] CR_LF = { 13, 10 };

	private static final Object INSERTION = new Object();

	private static final Pattern P_WORD = Pattern.compile("\\w+");

	protected static final PropertyFilter SEARCHABLE_PROPS = new PropertyFilter() {
		@Override
		public boolean eval(Prop prop) throws Exception {
			return U.isAssignableTo(prop.getType(), Number.class, String.class, Boolean.class, Enum.class, Date.class);
		}
	};

	private final long startedAt = System.currentTimeMillis();

	private final String filename;

	private final EntitySerializer serializer;

	private final EntityConstructor constructor;

	private final PropertySelector relPropSelector;

	private final ConcurrentMap<Tuple, RelPair> relPairs = new ConcurrentHashMap<Tuple, RelPair>();

	private final AtomicLong ids = new AtomicLong();

	private final AtomicLong lastChangedOn = new AtomicLong();

	private final AtomicLong lastPersistedOn = new AtomicLong();

	private final AtomicBoolean active = new AtomicBoolean(true);

	private final AtomicBoolean aOrB = new AtomicBoolean(true);

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	private final Thread persistor;

	private final AtomicBoolean insideTx = new AtomicBoolean(false);

	private final ConcurrentNavigableMap<Long, Rec> txChanges = new ConcurrentSkipListMap<Long, Rec>();

	private final ConcurrentNavigableMap<Long, Object> txInsertions = new ConcurrentSkipListMap<Long, Object>();

	private final ConcurrentLinkedQueue<Callback<Void>> txCallbacks = new ConcurrentLinkedQueue<Callback<Void>>();

	private final AtomicLong txIdCounter = new AtomicLong();

	private ConcurrentNavigableMap<Long, Rec> prevData = new ConcurrentSkipListMap<Long, Rec>();

	private ConcurrentNavigableMap<Long, Rec> data = new ConcurrentSkipListMap<Long, Rec>();

	public InMem(String filename, EntitySerializer serializer, EntityConstructor constructor,
			final Set<Class<?>> relClasses) {

		this.filename = filename;
		this.serializer = serializer;
		this.constructor = constructor;

		this.relPropSelector = new PropertyFilter() {
			@Override
			public boolean eval(Prop prop) throws Exception {
				for (Class<?> relCls : relClasses) {
					if (relCls.isAssignableFrom(prop.getRawType())) {
						return true;
					}
				}
				return false;
			}
		};

		if (filename != null && !filename.isEmpty()) {
			persistor = new Thread(new Runnable() {
				@Override
				public void run() {
					persist();
				}
			});
		} else {
			persistor = null;
		}
	}

	public void initAndLoad() {
		if (filename != null && !filename.isEmpty()) {
			if (currentFile().exists() && otherFile().exists()) {
				resolveDoubleFileInconsistency();
			}
			load();
			persistor.start();
		}
	}

	private void resolveDoubleFileInconsistency() {
		String file1 = currentFile().getName();
		String file2 = otherFile().getName();

		U.warn("The database was left in inconsistent state, both files exist!", "file1", file1, "file2", file2);

		long modif1, modif2;
		try {
			modif1 = (Long) loadMetadata(new FileInputStream(currentFile())).get(META_TIMESTAMP);
			modif2 = (Long) loadMetadata(new FileInputStream(otherFile())).get(META_TIMESTAMP);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		U.must(modif1 != modif2,
				"Cannot determine which database file to remove, please remove the incorrect file manually!");

		// delete the most recent file, since it wasn't written completely
		File recent = modif1 > modif2 ? currentFile() : otherFile();

		U.warn("The more recent database file is assumed incomplete, so it will be deleted!", "file", recent);

		recent.delete();
	}

	private void load() {
		try {
			if (currentFile().exists()) {
				loadFrom(new FileInputStream(currentFile()));
				aOrB.set(false);
			} else if (otherFile().exists()) {
				loadFrom(new FileInputStream(otherFile()));
				aOrB.set(true);
			}
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	public long insert(Object record) {
		sharedLock();
		try {
			long id = ids.incrementAndGet();
			Cls.setId(record, id);

			if (insideTx.get()) {
				if (txInsertions.putIfAbsent(id, INSERTION) != null) {
					throw new IllegalStateException("Cannot insert changelog record with existing ID: " + id);
				}
			}

			if (data.putIfAbsent(id, rec(record)) != null) {
				throw new IllegalStateException("Cannot insert record with existing ID: " + id);
			}

			updateChangesFromRels(record);

			lastChangedOn.set(System.currentTimeMillis());
			return id;
		} finally {
			sharedUnlock();
		}
	}

	private void updateChangesFromRels(Object entity) {
		for (Prop prop : Cls.propertiesOf(entity).select(relPropSelector)) {
			Object value = prop.get(entity);

			if (hasEntityLinks(value)) {
				EntityLinks links = entityLinks(value);

				long fromId = links.fromId();
				propageteRelChanges(entity, prop, links, fromId, links.addedRelIds(), links.removedRelIds());
			}
		}
	}

	private void deleteRelsFor(Object entity) {
		for (Prop prop : Cls.propertiesOf(entity).select(relPropSelector)) {
			Object value = prop.get(entity);

			if (hasEntityLinks(value)) {
				EntityLinks links = entityLinks(value);
				long fromId = links.fromId();
				propageteRelChanges(entity, prop, links, fromId, null, links.allRelIds());
			}
		}
	}

	private boolean hasEntityLinks(Object value) {
		return value instanceof EntityLinksContainer;
	}

	private EntityLinks entityLinks(Object value) {
		return ((EntityLinksContainer) value).getEntityLinks();
	}

	private void propageteRelChanges(Object entity, Prop prop, EntityLinks links, long fromId,
			Collection<Long> addToIds, Collection<Long> delToIds) {

		String rel = links.relationName();

		boolean inverse = rel.startsWith("^");
		if (inverse) {
			rel = rel.substring(1);
		}

		RelPair relPair = getRelPair(entity, prop, rel, inverse);

		if (addToIds != null) {
			for (long toId : addToIds) {
				if (!inverse) {
					relLink(rel, relPair.destProp, toId, relPair.srcProp, fromId);
				} else {
					relLink(rel, relPair.srcProp, toId, relPair.destProp, fromId);
				}
			}
		}

		if (delToIds != null) {
			for (long toId : delToIds) {
				if (!inverse) {
					relUnlink(rel, relPair.destProp, toId, relPair.srcProp, fromId);
				} else {
					relUnlink(rel, relPair.srcProp, toId, relPair.destProp, fromId);
				}
			}
		}
	}

	private RelPair getRelPair(Object entity, Prop prop, String rel, boolean inverse) {
		Class<?> cls = prop.getRawTypeArg(0);
		Class<?> srcType = inverse ? cls : entity.getClass();
		Class<?> destType = inverse ? entity.getClass() : cls;

		Tuple key = new Tuple(rel, srcType, destType);
		RelPair relPair = relPairs.get(key);

		Prop srcProp, destProp;

		if (relPair != null) {
			srcProp = relPair.srcProp;
			destProp = relPair.destProp;
		} else {
			String invRel = inverse ? rel : "^" + rel;
			Prop p = findRelProperty(cls, invRel, entity.getClass());
			srcProp = inverse ? p : prop;
			destProp = inverse ? prop : p;
			relPair = new RelPair(rel, srcType, destType, srcProp, destProp);
			relPairs.putIfAbsent(key, relPair);
		}

		return relPair;
	}

	private Prop findRelProperty(Class<?> fromCls, String rel, Class<?> toCls) {
		Object entity = !fromCls.isInterface() ? constructor.create(fromCls) : null;

		for (Prop prop : Cls.propertiesOf(fromCls).select(relPropSelector)) {

			String relName = null;

			if (!fromCls.isInterface()) {
				Object value = prop.get(entity);
				if (hasEntityLinks(value)) {
					EntityLinks links = entityLinks(value);
					relName = links.relationName();
				}
			} else {
				Relation relation = prop.getAnnotation(Relation.class);
				if (relation != null) {
					relName = relation.value();
				}
			}

			if (relName != null && relName.equals(rel)) {
				if (prop.getRawTypeArg(0).equals(toCls)) {
					return prop;
				}
			}
		}

		return null;
	}

	private void relLink(String relation, Prop srcProp, long fromId, Prop destProp, long toId) {
		if (srcProp != null && destProp != null) {
			Object from = get(fromId);
			EntityLinks srcRels = entityLinks(srcProp.get(from));
			srcRels.addRelTo(toId);
			update_(fromId, from, false);
		}
	}

	private void relUnlink(String relation, Prop srcProp, long fromId, Prop destProp, long toId) {
		if (srcProp != null && destProp != null) {
			Object from = get(fromId);
			EntityLinks srcRels = entityLinks(srcProp.get(from));
			srcRels.removeRelTo(toId);
			update_(fromId, from, false);
		}
	}

	public void delete(long id) {
		sharedLock();
		try {
			validateId(id);

			Rec removed = data.remove(id);

			if (insideTx.get()) {
				txChanges.putIfAbsent(id, removed);
			}

			Object entity = obj(removed);
			deleteRelsFor(entity);

			lastChangedOn.set(System.currentTimeMillis());

		} finally {
			sharedUnlock();
		}
	}

	public void delete(Object record) {
		delete(Cls.getId(record));
	}

	public <E> E get(long id) {
		sharedLock();
		try {
			return get_(id, true);
		} finally {
			sharedUnlock();
		}
	}

	public <E> E getIfExists(long id) {
		sharedLock();
		try {
			return get_(id, false);
		} finally {
			sharedUnlock();
		}
	}

	private <E> E get_(long id, boolean validateId) {
		if (validateId) {
			validateId(id);
		}
		Rec rec = data.get(id);

		if (rec != null) {
			E record = obj(rec);
			Cls.setId(record, id);
			return record;
		} else {
			return null;
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

	public void refresh(Object record) {
		sharedLock();
		try {

			long id = Cls.getId(record);
			validateId(id);
			Rec rec = getRec(id);
			obj(rec, record);
		} finally {
			sharedUnlock();
		}
	}

	private Rec getRec(long id) {
		Rec rec = data.get(id);
		if (rec == null) {
			throw invalidId(id);
		}
		return rec;
	}

	public void update(long id, Object record) {
		sharedLock();
		try {
			update_(id, record, true);
		} finally {
			sharedUnlock();
		}
	}

	private void update_(long id, Object record, boolean reflectRelChanges) {
		validateId(id);

		Cls.setId(record, id);

		Rec removed = data.replace(id, rec(record));

		if (insideTx.get()) {
			txChanges.putIfAbsent(id, removed);
		}

		if (removed == null) {
			throw new IllegalStateException("Cannot update non-existing record with ID=" + id);
		}

		if (reflectRelChanges) {
			updateChangesFromRels(record);
		}

		lastChangedOn.set(System.currentTimeMillis());
	}

	public void update(Object record) {
		update(Cls.getId(record), record);
	}

	public long persist(Object record) {
		long id = Cls.getId(record);
		if (id <= 0) {
			return insert(record);
		} else {
			update(id, record);
			return id;
		}
	}

	public long persistedIdOf(Object record) {
		long id = Cls.getId(record);
		if (id <= 0) {
			return insert(record);
		} else {
			return id;
		}
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

	protected <E> List<E> sorted(List<E> records, Comparator<E> orderBy) {
		if (orderBy != null) {
			Collections.sort(records, orderBy);
		}
		return records;
	}

	@SuppressWarnings("unchecked")
	public <E> List<E> getAll(long[] ids) {
		List<E> results = new ArrayList<E>(ids.length);

		sharedLock();
		try {

			for (long id : ids) {
				results.add((E) get_(id, true));
			}

			return results;
		} finally {
			sharedUnlock();
		}
	}

	@SuppressWarnings("unchecked")
	public <E> List<E> getAll(Collection<Long> ids) {
		List<E> results = new ArrayList<E>(ids.size());

		sharedLock();
		try {

			for (long id : ids) {
				results.add((E) get_(id, true));
			}

			return results;
		} finally {
			sharedUnlock();
		}
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

	public <E> List<E> find(final Class<E> clazz, final Predicate<E> match, final Comparator<E> orderBy) {
		final List<E> results = new ArrayList<E>();

		each(new Operation<E>() {
			@Override
			public void execute(E record) throws Exception {
				if (clazz.isAssignableFrom(record.getClass())) {
					if (match == null || match.eval(record)) {
						results.add(record);
					}
				}
			}
		});

		return sorted(results, orderBy);
	}

	public <E> List<E> find(String searchPhrase) {
		final String search = searchPhrase.toLowerCase();

		Predicate<E> match = new Predicate<E>() {
			@Override
			public boolean eval(E record) throws Exception {

				if (record.getClass().getSimpleName().toLowerCase().contains(search)) {
					return true;
				}

				for (Prop prop : Cls.propertiesOf(record).select(SEARCHABLE_PROPS)) {
					String s = String.valueOf(prop.get(record)).toLowerCase();
					if (s.contains(search)) {
						return true;
					}
				}
				return false;
			}
		};

		return find(match);
	}

	public <E> List<E> find(final Class<E> clazz, final String query, final Object... args) {

		Predicate<E> match = new Predicate<E>() {
			@Override
			public boolean eval(E record) throws Exception {
				return clazz.isAssignableFrom(record.getClass()) && matches(record, query, args);
			}
		};

		return find(match);
	}

	public boolean matches(Object record, String query, Object... args) {

		if (query == null || query.isEmpty()) {
			return true;
		}

		if (P_WORD.matcher(query).matches() && args.length == 1) {
			Object val = Cls.getPropValue(record, query, null);
			Object arg = args[0];
			return val == arg || (val != null && val.equals(arg));
		}

		throw new RuntimeException("Query not supported: " + query);
	}

	public <E> void each(final Operation<E> lambda) {
		sharedLock();
		try {

			for (Entry<Long, Rec> entry : data.entrySet()) {
				E record = obj(entry.getValue());
				Cls.setId(record, entry.getKey());

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

	/**
	 * Simple, persisted in-memory NoSQL DB, based on {@link ConcurrentSkipListMap}.<br>
	 * 
	 * ACID transactional semantics:<br>
	 * - Atomicity with automatic rollback in case of exception,<br>
	 * - Consistency - only with constraints enforced programmatically inside transaction,<br>
	 * - Isolation is serializable (with global lock),<br>
	 * - Durability through on-commit callbacks (this method is blocking).<br>
	 */
	public void transaction(Runnable transaction, boolean readOnly) {

		final AtomicReference<Throwable> error = new AtomicReference<Throwable>();
		final CountDownLatch latch = new CountDownLatch(1);

		Callback<Void> txCallback = new Callback<Void>() {
			@Override
			public void onDone(Void result, Throwable e) {
				latch.countDown();
				error.set(e);
			}
		};

		transaction(transaction, readOnly, txCallback);

		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		if (error.get() != null) {
			throw new RuntimeException("Transaction failure!", error.get());
		}
	}

	/**
	 * ACID transactional semantics:<br>
	 * - Atomicity with automatic rollback in case of exception,<br>
	 * - Consistency - only with constraints enforced programmatically inside transaction,<br>
	 * - Isolation is serializable (with global lock),<br>
	 * - Durability through on-commit callbacks.<br>
	 */
	public void transaction(Runnable transaction, boolean readOnly, Callback<Void> txCallback) {
		globalLock();

		txIdCounter.set(ids.get());
		txChanges.clear();
		txInsertions.clear();

		insideTx.set(true);

		try {
			transaction.run();

			if (txCallback != null) {
				txCallbacks.add(txCallback);
			}

		} catch (Throwable e) {
			U.error("Error in transaction, rolling back", e);
			txRollback();
			if (txCallback != null) {
				txCallback.onDone(null, e);
			}

		} finally {
			txChanges.clear();
			txInsertions.clear();
			insideTx.set(false);
			globalUnlock();
		}
	}

	private void txRollback() {
		ids.set(txIdCounter.get());

		for (Entry<Long, Rec> e : txChanges.entrySet()) {
			Long id = e.getKey();
			Rec value = e.getValue();
			U.must(value != null, "Cannot have null value!");
			data.put(id, value);
		}

		for (Entry<Long, Object> e : txInsertions.entrySet()) {
			// rollback insert operation
			Long id = e.getKey();
			Object value = e.getValue();
			U.must(value == INSERTION, "Expected insertion mode!");

			Rec inserted = data.remove(id);
			U.must(inserted != null, "Cannot have null insertion!");
		}
	}

	private <T> T get_(long id, Class<T> clazz) {
		validateId(id);
		Rec rec = data.get(id);
		if (rec != null) {
			T record = obj(rec, clazz);
			Cls.setId(record, id);
			return record;
		} else {
			return null;
		}
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
		if (!data.containsKey(id)) {
			throw invalidId(id);
		}
	}

	private IllegalArgumentException invalidId(long id) {
		return new IllegalArgumentException("Cannot find DB record with id=" + id);
	}

	public void saveTo(final OutputStream output) {
		globalLock();

		try {
			PrintWriter out = new PrintWriter(output);

			out.println(new String(serializer.serialize(metadata())));

			for (Entry<Long, Rec> entry : data.entrySet()) {
				out.println(new String(entry.getValue().bytes));
			}

			out.close();
		} finally {
			globalUnlock();
		}
	}

	public Map<String, Object> loadMetadata(InputStream in) {
		globalLock();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String line = reader.readLine();
			U.must(line != null, "Missing meta-data at the first line in the database file!");

			Map<String, Object> meta = U.map();
			serializer.deserialize(line.getBytes(), meta);

			reader.close();

			return meta;

		} catch (IOException e) {
			throw new RuntimeException("Cannot load meta-data from database!", e);
		} finally {
			globalUnlock();
		}
	}

	public void loadFrom(InputStream in) {
		globalLock();

		try {
			data.clear();

			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String line = reader.readLine();
			byte[] bytes = line.getBytes();

			U.must(line != null, "Missing meta-data at the first line in the database file!");

			Map<String, Object> meta = U.map();
			serializer.deserialize(bytes, meta);

			U.info("Database meta-data", META_TIMESTAMP, meta.get(META_TIMESTAMP), META_UPTIME, meta.get(META_UPTIME));

			while ((line = reader.readLine()) != null) {
				bytes = line.getBytes();
				Map<String, Object> map = U.map();
				serializer.deserialize(bytes, map);

				Number idNum = (Number) map.get("id");
				U.must(idNum != null, "Found DB record without ID: %s", line);
				long id = idNum.longValue();
				String className = ((String) map.get("_class"));

				Class<?> type;
				try {
					type = Class.forName(className);
				} catch (ClassNotFoundException e) {
					type = null;
				}

				data.put(id, new Rec(type, bytes));

				if (id > ids.get()) {
					ids.set(id);
				}
			}

			prevData = new ConcurrentSkipListMap<Long, Rec>(data);

			reader.close();

		} catch (IOException e) {
			throw new RuntimeException("Cannot load database!", e);
		} finally {
			globalUnlock();
		}
	}

	private void persistTo(RandomAccessFile file) throws IOException {
		file.write(serializer.serialize(metadata()));
		file.write(CR_LF);
		for (Entry<Long, Rec> entry : data.entrySet()) {
			file.write(entry.getValue().bytes);
			file.write(CR_LF);
		}
	}

	private Map<String, Object> metadata() {
		Map<String, Object> meta = new HashMap<String, Object>();

		long now = System.currentTimeMillis();

		meta.put(META_TIMESTAMP, now);
		meta.put(META_UPTIME, now - startedAt);

		return meta;
	}

	@SuppressWarnings("unchecked")
	private void persistData() {
		globalLock();

		final ConcurrentNavigableMap<Long, Rec> copy;
		Callback<Void>[] callbacks;

		try {
			if (data.isEmpty() && txCallbacks.isEmpty()) {
				return;
			}

			copy = new ConcurrentSkipListMap<Long, Rec>(data);

			callbacks = txCallbacks.toArray(new Callback[txCallbacks.size()]);
			txCallbacks.clear();

		} finally {
			globalUnlock();
		}

		if (lastChangedOn.get() < lastPersistedOn.get()) {
			invokeCallbacks(callbacks, null);
			return;
		}

		lastPersistedOn.set(System.currentTimeMillis());

		try {
			File file = currentFile();

			if (file.exists()) {
				throw new IllegalStateException("Cannot save the database, file already exists: "
						+ file.getAbsolutePath());
			}

			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			persistTo(raf);
			raf.getChannel().force(false);
			raf.close();

			prevData = copy;
			boolean isA = aOrB.get();
			U.must(aOrB.compareAndSet(isA, !isA), "DB persistence file switching error!");

			File oldFile = currentFile();
			oldFile.delete();

		} catch (IOException e) {

			invokeCallbacks(callbacks, e);

			data = new ConcurrentSkipListMap<Long, Rec>(prevData);

			throw new RuntimeException("Cannot persist database changes!", e);
		}

		invokeCallbacks(callbacks, null);
	}

	private void invokeCallbacks(Callback<Void>[] callbacks, Throwable e) {
		for (Callback<Void> callback : callbacks) {
			try {
				callback.onDone(null, e);
			} catch (Throwable e2) {
				U.error("Transaction callback error", e2);
			}
		}
	}

	private File currentFile() {
		return new File(filenameWithSuffix(aOrB.get() ? SUFFIX_A : SUFFIX_B));
	}

	private File otherFile() {
		return new File(filenameWithSuffix(!aOrB.get() ? SUFFIX_A : SUFFIX_B));
	}

	private String filenameWithSuffix(String suffixAorB) {
		return filename.replace(".db", "-" + suffixAorB + ".db");
	}

	private void persist() {
		while (!Thread.interrupted()) {
			try {
				persistData();
			} catch (Exception e1) {
				U.error("Failed to persist data!", e1);
			}

			if (active.get()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			} else {
				try {
					persistData();
				} catch (Exception e1) {
					U.error("Failed to persist data!", e1);
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

		new File(filename).delete();
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
		new File(filenameWithSuffix(SUFFIX_A)).delete();
		new File(filenameWithSuffix(SUFFIX_B)).delete();
	}

	private Rec rec(Object record) {
		return new Rec(record.getClass(), serializer.serialize(record));
	}

	@SuppressWarnings("unchecked")
	private <T> T obj(Rec rec) {
		Class<T> destType = (Class<T>) rec.type;
		T dest = constructor.create(destType);
		return (T) obj(rec, dest);
	}

	private <T> T obj(Rec rec, Class<T> destType) {
		T dest = constructor.create(destType);
		return (T) obj(rec, dest);
	}

	private <T> T obj(Rec rec, T destination) {
		serializer.deserialize(rec.bytes, destination);
		return destination;
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
