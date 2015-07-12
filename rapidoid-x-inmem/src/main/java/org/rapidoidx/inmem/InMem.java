package org.rapidoidx.inmem;

/*
 * #%L
 * rapidoid-x-inmem
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import java.io.Serializable;
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
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Rel;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Prop;
import org.rapidoid.beany.PropertyFilter;
import org.rapidoid.cls.Cls;
import org.rapidoid.cls.TypeKind;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.job.Jobs;
import org.rapidoid.lambda.Operation;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.log.Log;
import org.rapidoid.scan.Scan;
import org.rapidoid.security.Secure;
import org.rapidoid.tuple.Tuple;
import org.rapidoid.util.OptimisticConcurrencyControlException;
import org.rapidoid.util.SuccessException;
import org.rapidoid.util.U;

class Rec implements Serializable {

	private static final long serialVersionUID = 1669889751748592446L;

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
@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class InMem implements Serializable {

	private static final long serialVersionUID = -200957806998151795L;

	private static final String SUPERADMIN = "SUPERADMIN";

	private static final String ID = "id";

	private static final String VERSION = "version";

	private static final String CREATED_BY = "createdBy";

	private static final String CREATED_ON = "createdOn";

	private static final String LAST_UPDATED_BY = "lastUpdatedBy";

	private static final String LAST_UPDATED_ON = "lastUpdatedOn";

	private static final String META_UPTIME = "uptime";

	private static final String META_TIMESTAMP = "timestamp";

	private static final String SUFFIX_B = "b";

	private static final String SUFFIX_A = "a";

	private static final byte[] CR_LF = { 13, 10 };

	private static final Object INSERTION = new Object();

	private static final Pattern P_WORD = Pattern.compile("\\w+");

	@SuppressWarnings("serial")
	protected static final PropertyFilter SEARCHABLE_PROPS = new PropertyFilter() {
		@Override
		public boolean eval(Prop prop) throws Exception {
			return Cls
					.isAssignableTo(prop.getType(), Number.class, String.class, Boolean.class, Enum.class, Date.class);
		}
	};

	private final InMemData data;

	private final String asUsername;

	private Thread persistor;

	private final boolean sudo;

	@SuppressWarnings("serial")
	public InMem(String filename, EntitySerializer serializer, EntityConstructor constructor,
			final Set<Class<?>> relClasses, String asUsername) {

		this(new InMemData(filename, serializer, constructor, new PropertyFilter() {
			@Override
			public boolean eval(Prop prop) throws Exception {
				for (Class<?> relCls : relClasses) {
					if (relCls.isAssignableFrom(prop.getRawType())) {
						return true;
					}
				}
				return false;
			}
		}), asUsername, null, false);

		// TODO separate persistor thread into a new class and separate a DB store
		this.persistor = (filename != null && !filename.isEmpty()) ? new Thread(new Runnable() {
			@Override
			public void run() {
				persist();
			}
		}) : null;
	}

	private InMem(InMemData data, String asUsername, Thread persistor, boolean sudo) {
		this.data = data;
		this.asUsername = asUsername;
		this.persistor = persistor;
		this.sudo = sudo;
	}

	protected String username() {
		if (sudo) {
			return SUPERADMIN;
		}
		return asUsername != null ? asUsername : Secure.username();
	}

	public void initAndLoad() {
		if (data.filename != null && !data.filename.isEmpty()) {
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

		Log.warn("The database was left in inconsistent state, both files exist!", "file1", file1, "file2", file2);

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

		Log.warn("The more recent database file is assumed incomplete, so it will be deleted!", "file", recent);

		recent.delete();
	}

	private void load() {
		try {
			if (currentFile().exists()) {
				loadFrom(new FileInputStream(currentFile()));
				data.aOrB.set(false);
			} else if (otherFile().exists()) {
				loadFrom(new FileInputStream(otherFile()));
				data.aOrB.set(true);
			}
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	public long insert(Object record) {
		return _insert(record, true);
	}

	private long _insert(Object record, boolean failOnReadOnlyTx) {
		U.notNull(record, "record");
		secureInsert(record);

		sharedLock();
		try {
			if (failOnReadOnlyTx) {
				failIfReadonlyTx();
			}

			long id = data.ids.incrementAndGet();
			Beany.setId(record, id);

			// Optimistic concurrency control through the "version" property
			if (Beany.hasProperty(record, VERSION)) {
				// FIXME rollback version in TX fails
				Beany.setPropValue(record, VERSION, 1);
			}

			Date now = new Date();

			if (Beany.hasProperty(record, CREATED_BY)) {
				Beany.setPropValue(record, CREATED_BY, username());
			}

			if (Beany.hasProperty(record, CREATED_ON)) {
				Beany.setPropValue(record, CREATED_ON, now);
			}

			if (Beany.hasProperty(record, LAST_UPDATED_BY)) {
				Beany.setPropValue(record, LAST_UPDATED_BY, username());
			}

			if (Beany.hasProperty(record, LAST_UPDATED_ON)) {
				Beany.setPropValue(record, LAST_UPDATED_ON, now);
			}

			if (data.insideTx.get()) {
				if (data.txInsertions.putIfAbsent(id, INSERTION) != null) {
					throw new IllegalStateException("Cannot insert changelog record with existing ID: " + id);
				}
			}

			if (data.data.putIfAbsent(id, rec(record)) != null) {
				throw new IllegalStateException("Cannot insert record with existing ID: " + id);
			}

			updateChangesFromRels(record);

			data.lastChangedOn.set(System.currentTimeMillis());

			Log.audit("Inserted DB record", "id", id);
			return id;
		} finally {
			sharedUnlock();
		}
	}

	private void updateChangesFromRels(Object entity) {
		for (Prop prop : Beany.propertiesOf(entity).select(data.relPropSelector)) {
			Object value = prop.getRaw(entity);

			if (hasEntityLinks(value)) {
				EntityLinks links = entityLinks(value);

				long fromId = links.fromId();
				propageteRelChanges(entity, prop, links, fromId, links.addedRelIds(), links.removedRelIds());
			}
		}
	}

	private void deleteRelsFor(Object entity) {
		for (Prop prop : Beany.propertiesOf(entity).select(data.relPropSelector)) {
			Object value = prop.getRaw(entity);

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
		Class<? extends Object> entCls = Cls.unproxy(entity.getClass());

		Class<?> srcType = inverse ? cls : entCls;
		Class<?> destType = inverse ? entCls : cls;

		Tuple key = new Tuple(rel, srcType, destType);
		RelPair relPair = data.relPairs.get(key);

		Prop srcProp, destProp;

		if (relPair != null) {
			srcProp = relPair.srcProp;
			destProp = relPair.destProp;
		} else {
			String invRel = inverse ? rel : "^" + rel;
			Prop p = findRelProperty(cls, invRel, entCls);
			srcProp = inverse ? p : prop;
			destProp = inverse ? prop : p;

			relPair = new RelPair(rel, srcType, destType, srcProp, destProp);
			data.relPairs.putIfAbsent(key, relPair);

			if (srcType == null || srcProp == null || destType == null || destProp == null) {
				Log.warn("Incomplete relation pair!", "relation", relPair);
			}
		}

		return relPair;
	}

	private Prop findRelProperty(Class<?> fromCls, String rel, Class<?> toCls) {
		Object entity = !fromCls.isInterface() ? data.constructor.create(fromCls) : null;

		for (Prop prop : Beany.propertiesOf(fromCls).select(data.relPropSelector)) {

			String relName = null;

			if (!fromCls.isInterface()) {
				Object value = prop.getRaw(entity);
				if (hasEntityLinks(value)) {
					EntityLinks links = entityLinks(value);
					relName = links.relationName();
				}
			} else {
				Rel relation = prop.getAnnotation(Rel.class);
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

		Log.warn("Didn't find inverse relation property!", "relation", rel, "from", fromCls, "to", toCls);
		return null;
	}

	private void relLink(String relation, Prop srcProp, long fromId, Prop destProp, long toId) {
		if (srcProp != null && destProp != null) {
			Object from = get_(fromId, true);
			EntityLinks srcRels = entityLinks(srcProp.getRaw(from));
			srcRels.addRelTo(toId);
			update_(fromId, from, false, false);
		}
	}

	private void relUnlink(String relation, Prop srcProp, long fromId, Prop destProp, long toId) {
		if (srcProp != null && destProp != null) {
			Object from = get_(fromId, true);
			EntityLinks srcRels = entityLinks(srcProp.getRaw(from));
			srcRels.removeRelTo(toId);
			update_(fromId, from, false, false);
		}
	}

	public void delete(long id) {
		sharedLock();

		try {
			failIfReadonlyTx();
			validateId(id);

			Rec old = data.data.get(id);
			Object entity = obj(old);
			secureDelete(entity);

			boolean removed = data.data.remove(id, old);
			occErrorIf(!removed, "Concurrent modification occured while deleting the object with ID=%s!", id);

			if (data.insideTx.get()) {
				data.txChanges.putIfAbsent(id, old);
			}

			deleteRelsFor(entity);

			data.lastChangedOn.set(System.currentTimeMillis());

			Log.audit("Deleted DB record", "id", id);

		} finally {
			sharedUnlock();
		}
	}

	public void delete(Object record) {
		U.argMust(Cls.kindOf(record) == TypeKind.OBJECT, "Invalid record type: %s", Cls.of(record));
		delete(Beany.getLongId(record));
	}

	public <E> E get(long id) {
		sharedLock();
		try {
			return getIfAllowed(id, true);
		} finally {
			sharedUnlock();
		}
	}

	public <E> E getIfExists(long id) {
		sharedLock();
		try {
			return getIfAllowed(id, false);
		} finally {
			sharedUnlock();
		}
	}

	private <E> E get_(long id, boolean validateId) {
		if (validateId) {
			validateId(id);
		}
		Rec rec = data.data.get(id);

		if (rec != null) {
			E record = obj(rec);
			Beany.setId(record, id);
			return record;
		} else {
			return null;
		}
	}

	public <E> E get(long id, Class<E> clazz) {
		sharedLock();
		try {
			validateId(id);
			return getIfAllowed(id, clazz);
		} finally {
			sharedUnlock();
		}
	}

	public void refresh(Object record) {
		sharedLock();
		try {

			long id = Beany.getLongId(record);
			validateId(id);
			Rec rec = getRec(id);

			Object tmp = obj(rec);
			secureRead(tmp);

			obj(rec, record);
			resetInvisibleColumns(record);
		} finally {
			sharedUnlock();
		}
	}

	private Rec getRec(long id) {
		Rec rec = data.data.get(id);
		if (rec == null) {
			throw invalidId(id);
		}
		return rec;
	}

	public void update(long id, Object record) {
		sharedLock();
		try {
			update_(id, record, true, true);
		} finally {
			sharedUnlock();
		}
	}

	private void update_(long id, Object record, boolean reflectRelChanges, boolean checkSecurity) {

		failIfReadonlyTx();
		validateId(id);

		Rec old = data.data.get(id);
		Object entity = obj(old);

		if (checkSecurity) {
			secureUpdate(entity);
		}

		// Optimistic concurrency control through the "version" property
		Long oldVersion = U.or(Beany.getPropValueOfType(entity, VERSION, Long.class, null), 0L);
		Long recordVersion = U.or(Beany.getPropValueOfType(record, VERSION, Long.class, null), 0L);

		occErrorIf(!U.eq(oldVersion, recordVersion),
				"Concurrent modification occured while updating the object with ID=%s!", id);

		Beany.setId(record, id);

		if (!sudo && checkSecurity) {
			boolean canUpdate = false;
			for (Prop prop : Beany.propertiesOf(record)) {
				if (!Secure.getPropertyPermissions(username(), entity.getClass(), entity, prop.getName()).change) {
					prop.set(record, prop.get(entity));
				} else {
					canUpdate = true;
				}
			}
			U.secure(canUpdate, "Not enough privileges to update any column of %s!", entity.getClass().getSimpleName());
		}

		// Optimistic concurrency control through the "version" property
		if (Beany.hasProperty(record, VERSION)) {
			Beany.setPropValue(record, VERSION, oldVersion + 1);
		}

		if (checkSecurity) {
			secureUpdate(record);
		}

		if (Beany.hasProperty(record, LAST_UPDATED_BY)) {
			Beany.setPropValue(record, LAST_UPDATED_BY, username());
		}

		if (Beany.hasProperty(record, LAST_UPDATED_ON)) {
			Beany.setPropValue(record, LAST_UPDATED_ON, new Date());
		}

		boolean updated = data.data.replace(id, old, rec(record));

		occErrorIf(!updated, "Concurrent modification occured while updating the object with ID=%s!", id);

		if (data.insideTx.get()) {
			data.txChanges.putIfAbsent(id, old);
		}

		if (old == null) {
			throw new IllegalStateException("Cannot update non-existing record with ID=" + id);
		}

		if (reflectRelChanges) {
			updateChangesFromRels(record);
		}

		data.lastChangedOn.set(System.currentTimeMillis());

		Log.audit("Updated DB record", "id", id);
	}

	private static void occErrorIf(boolean errCond, String msg, long id) {
		if (errCond) {
			throw new OptimisticConcurrencyControlException(U.format(msg, id), id);
		}
	}

	public void update(Object record) {
		update(Beany.getLongId(record), record);
	}

	public long persist(Object record) {
		Long id = Beany.getLongIdIfExists(record);
		if (id == null || id <= 0) {
			return insert(record);
		} else {
			update(id, record);
			return id;
		}
	}

	public long insertOrGetId(Object record) {
		Long id = Beany.getLongIdIfExists(record);
		if (id == null || id <= 0) {
			return insert(record);
		} else {
			return id;
		}
	}

	public <T> T readColumn(long id, String column) {
		sharedLock();
		try {
			Object record = getIfAllowed(id, true);
			secureReadColumn(record, column);
			T value = Beany.getPropValue(record, column);
			return value;
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
				results.add((E) getIfAllowed(id, true));
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
				results.add((E) getIfAllowed(id, true));
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
				if (canRead(record) && match.eval(record)) {
					results.add(record);
				}
			}

		});

		return results;
	}

	@SuppressWarnings("unchecked")
	public <E> List<E> find(Iterable<Long> ids) {
		List<E> results = new ArrayList<E>();

		sharedLock();
		try {

			for (long id : ids) {
				E entity = (E) getIfAllowedOrNull(id, true);
				if (entity != null) {
					results.add(entity);
				}
			}

			return results;
		} finally {
			sharedUnlock();
		}
	}

	private <E> E getIfAllowed(long id, boolean validateId) {
		E record = get_(id, validateId);
		secureRead(record);
		resetInvisibleColumns(record);
		return record;
	}

	private <E> E getIfAllowedOrNull(long id, boolean validateId) {
		E record = get_(id, validateId);
		if (canRead(record)) {
			resetInvisibleColumns(record);
			return record;
		} else {
			return null;
		}
	}

	private <E> E getIfAllowed(long id, Class<E> type) {
		E record = get_(id, type);
		secureRead(record);
		resetInvisibleColumns(record);
		return record;
	}

	public <E> List<E> find(final Class<E> clazz, final Predicate<E> match, final Comparator<E> orderBy) {

		Predicate<E> match2 = new Predicate<E>() {
			@Override
			public boolean eval(E record) throws Exception {
				return (clazz == null || clazz.isAssignableFrom(record.getClass()))
						&& (match == null || match.eval(record));
			}
		};

		return sorted(find(match2), orderBy);
	}

	public <E> List<E> find(String searchPhrase) {
		final String search = searchPhrase.toLowerCase();

		Predicate<E> match = new Predicate<E>() {
			@Override
			public boolean eval(E record) throws Exception {

				if (record.getClass().getSimpleName().toLowerCase().contains(search)) {
					return true;
				}

				for (Prop prop : Beany.propertiesOf(record).select(SEARCHABLE_PROPS)) {
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

	public static boolean matches(Object record, String query, Object... args) {

		if (query == null || query.isEmpty()) {
			return true;
		}

		if (P_WORD.matcher(query).matches() && args.length == 1) {
			Object val = Beany.getPropValue(record, query, null);
			Object arg = args[0];
			return val == arg || (val != null && val.equals(arg));
		}

		throw new RuntimeException("Query not supported: " + query);
	}

	public <E> void each(final Operation<E> lambda) {
		sharedLock();
		try {

			for (Entry<Long, Rec> entry : data.data.entrySet()) {
				E record = obj(entry.getValue());
				Beany.setId(record, entry.getKey());

				if (canRead(record)) {
					try {
						lambda.execute(record);
					} catch (ClassCastException e) {
						// ignore, cast exceptions are expected
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
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

		data.txIdCounter.set(data.ids.get());
		data.txChanges.clear();
		data.txInsertions.clear();
		data.txReadonly.set(readOnly);
		data.insideTx.set(true);

		boolean success = false;
		try {
			transaction.run();
			success = true;

		} catch (Throwable e) {
			if (SuccessException.isSuccess(e)) {
				success = true;
				throw U.rte(e);
			} else {
				Log.error("Error in transaction, rolling back", e);
				txRollback();
				if (txCallback != null) {
					Jobs.call(txCallback, null, e);
					txCallback = null;
				}
			}

		} finally {
			data.txChanges.clear();
			data.txInsertions.clear();
			data.insideTx.set(false);

			if (persistor != null) {
				if (success && txCallback != null) {
					data.txCallbacks.add(txCallback);
				}
			} else {
				if (success && txCallback != null) {
					Jobs.call(txCallback, null, null);
				}
			}

			globalUnlock();
		}
	}

	private void txRollback() {
		data.ids.set(data.txIdCounter.get());

		for (Entry<Long, Rec> e : data.txChanges.entrySet()) {
			Long id = e.getKey();
			Rec value = e.getValue();
			U.must(value != null, "Cannot have null value!");
			data.data.put(id, value);
		}

		for (Entry<Long, Object> e : data.txInsertions.entrySet()) {
			// rollback insert operation
			Long id = e.getKey();
			Object value = e.getValue();
			U.must(value == INSERTION, "Expected insertion mode!");

			Rec inserted = data.data.remove(id);
			U.must(inserted != null, "Cannot have null insertion!");
		}
	}

	private <T> T get_(long id, Class<T> clazz) {
		validateId(id);
		Rec rec = data.data.get(id);
		if (rec != null) {
			T record = obj(rec, clazz);
			Beany.setId(record, id);
			return record;
		} else {
			return null;
		}
	}

	private void sharedLock() {
		data.lock.readLock().lock();
	}

	private void sharedUnlock() {
		data.lock.readLock().unlock();
	}

	private void globalLock() {
		data.lock.writeLock().lock();
	}

	private void globalUnlock() {
		data.lock.writeLock().unlock();
	}

	private void validateId(long id) {
		if (!data.data.containsKey(id)) {
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

			out.println(new String(data.serializer.serialize(metadata())));

			for (Entry<Long, Rec> entry : data.data.entrySet()) {
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
			data.serializer.deserialize(line.getBytes(), meta);

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
			data.data.clear();

			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String line = reader.readLine();
			byte[] bytes = line.getBytes();

			U.must(line != null, "Missing meta-data at the first line in the database file!");

			Map<String, Object> meta = U.map();
			data.serializer.deserialize(bytes, meta);

			Log.info("Database meta-data", META_TIMESTAMP, meta.get(META_TIMESTAMP), META_UPTIME, meta.get(META_UPTIME));

			while ((line = reader.readLine()) != null) {
				bytes = line.getBytes();
				Map<String, Object> map = U.map();
				data.serializer.deserialize(bytes, map);

				Object idObj = map.get(ID);
				U.must(idObj != null, "Found DB record without ID: %s", line);

				long id = Cls.convert(idObj, Long.class);
				String className = ((String) map.get("_class"));

				String[] nameParts = className.split("\\.");
				String simpleName = nameParts[nameParts.length - 1];
				List<Class<?>> classes = Scan.byName(simpleName, null, null);

				if (classes.size() == 1) {
					Class<?> type = classes.get(0);
					data.data.put(id, new Rec(type, bytes));

					if (id > data.ids.get()) {
						data.ids.set(id);
					}

				} else {
					if (classes.isEmpty()) {
						Log.error("Cannot find the class of a DB record!", "id", id, "class", className);
					} else {
						Log.error("Found more than 1 class of a DB record!", "id", id, "class", className);
					}
				}
			}

			data.prevData = new ConcurrentSkipListMap<Long, Rec>(data.data);

			reader.close();

		} catch (IOException e) {
			throw new RuntimeException("Cannot load database!", e);
		} finally {
			globalUnlock();
		}
	}

	private void persistTo(RandomAccessFile file) throws IOException {
		file.write(data.serializer.serialize(metadata()));
		file.write(CR_LF);
		for (Entry<Long, Rec> entry : data.data.entrySet()) {
			file.write(entry.getValue().bytes);
			file.write(CR_LF);
		}
	}

	private Map<String, Object> metadata() {
		Map<String, Object> meta = new HashMap<String, Object>();

		long now = System.currentTimeMillis();

		meta.put(META_TIMESTAMP, now);
		meta.put(META_UPTIME, now - data.startedAt);

		return meta;
	}

	@SuppressWarnings("unchecked")
	private void persistData() {
		globalLock();

		final ConcurrentNavigableMap<Long, Rec> copy;
		Callback<Void>[] callbacks;

		try {
			if (data.data.isEmpty() && data.txCallbacks.isEmpty()) {
				return;
			}

			copy = new ConcurrentSkipListMap<Long, Rec>(data.data);

			callbacks = data.txCallbacks.toArray(new Callback[data.txCallbacks.size()]);
			data.txCallbacks.clear();

		} finally {
			globalUnlock();
		}

		if (data.lastChangedOn.get() < data.lastPersistedOn.get()) {
			invokeCallbacks(callbacks, null);
			return;
		}

		data.lastPersistedOn.set(System.currentTimeMillis());

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

			data.prevData = copy;
			boolean isA = data.aOrB.get();
			U.must(data.aOrB.compareAndSet(isA, !isA), "DB persistence file switching error!");

			File oldFile = currentFile();
			oldFile.delete();

		} catch (IOException e) {

			invokeCallbacks(callbacks, e);

			data.data = new ConcurrentSkipListMap<Long, Rec>(data.prevData);

			throw new RuntimeException("Cannot persist database changes!", e);
		}

		invokeCallbacks(callbacks, null);
	}

	private void invokeCallbacks(Callback<Void>[] callbacks, Throwable e) {
		for (Callback<Void> callback : callbacks) {
			try {
				callback.onDone(null, e);
			} catch (Throwable e2) {
				Log.error("Transaction callback error", e2);
			}
		}
	}

	private File currentFile() {
		return new File(filenameWithSuffix(data.aOrB.get() ? SUFFIX_A : SUFFIX_B));
	}

	private File otherFile() {
		return new File(filenameWithSuffix(!data.aOrB.get() ? SUFFIX_A : SUFFIX_B));
	}

	private String filenameWithSuffix(String suffixAorB) {
		return data.filename.replace(".db", "-" + suffixAorB + ".db");
	}

	private void persist() {
		while (!Thread.interrupted()) {
			try {
				persistData();
			} catch (Exception e1) {
				Log.error("Failed to persist data!", e1);
			}

			if (data.active.get()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			} else {
				try {
					persistData();
				} catch (Exception e1) {
					Log.error("Failed to persist data!", e1);
				}
				return;
			}
		}
	}

	public void shutdown() {
		data.active.set(false);

		try {
			if (persistor != null) {
				persistor.join();
			}
		} catch (InterruptedException e) {}

		if (data.filename != null) {
			new File(data.filename).delete();
		}
	}

	public boolean isActive() {
		return data.active.get();
	}

	@Override
	public String toString() {
		return super.toString() + "[filename=" + data.filename + "]";
	}

	public void start() {
		// TODO implement start after shutdown
		if (!data.active.get()) {
			throw new IllegalStateException("Starting the database after shutdown is not implemented yet!");
		}
	}

	public void halt() {
		if (data.active.get()) {
			data.active.set(false);

			persistor.interrupt();
			try {
				persistor.join();
			} catch (InterruptedException e) {}
		}
	}

	public void destroy() {
		halt();
		new File(filenameWithSuffix(SUFFIX_A)).delete();
		new File(filenameWithSuffix(SUFFIX_B)).delete();
	}

	private Rec rec(Object record) {
		return new Rec(record.getClass(), data.serializer.serialize(record));
	}

	@SuppressWarnings("unchecked")
	private <T> T obj(Rec rec) {
		Class<T> destType = (Class<T>) rec.type;
		U.notNull(destType, "DB record type");
		T dest = data.constructor.create(destType);
		return (T) obj(rec, dest);
	}

	private <T> T obj(Rec rec, Class<T> destType) {
		T dest = data.constructor.create(destType);
		return (T) obj(rec, dest);
	}

	private <T> T obj(Rec rec, T destination) {
		data.serializer.deserialize(rec.bytes, destination);
		return destination;
	}

	public int size() {
		globalLock();
		try {
			return data.data.size();
		} finally {
			globalUnlock();
		}
	}

	public void clear() {
		globalLock();
		try {

			failIfReadonlyTx();

			for (Entry<Long, Rec> entry : data.data.entrySet()) {
				long id = entry.getKey();
				delete(id);
			}

		} finally {
			globalUnlock();
		}
	}

	public InMem as(String username) {
		return new InMem(data, username, persistor, false);
	}

	public InMem sudo() {
		return new InMem(data, null, persistor, true);
	}

	private boolean canRead(Object record) {
		return record == null || sudo || Secure.canRead(username(), record);
	}

	private boolean canInsert(Object record) {
		return record == null || sudo || Secure.canInsert(username(), record);
	}

	private boolean canUpdate(Object record) {
		return record == null || sudo || Secure.canUpdate(username(), record);
	}

	private boolean canDelete(Object record) {
		return record == null || sudo || Secure.canDelete(username(), record);
	}

	private boolean canReadColumn(Object record, String column) {
		return record == null || sudo || Secure.canReadProperty(username(), record, column);
	}

	private boolean canUpdateColumn(Object record, String column) {
		return record == null || sudo || Secure.canUpdateProperty(username(), record, column);
	}

	private void secureRead(Object record) {
		U.secure(canRead(record), "Not enough privileges to read the record!");
	}

	private void secureInsert(Object record) {
		U.secure(canInsert(record), "Not enough privileges to insert the record!");
	}

	private void secureUpdate(Object record) {
		U.secure(canUpdate(record), "Not enough privileges to update the record!");
	}

	private void secureDelete(Object record) {
		U.secure(canDelete(record), "Not enough privileges to delete the record!");
	}

	private void secureReadColumn(Object record, String column) {
		U.secure(canReadColumn(record, column), "Not enough privileges to read the column: %s!", column);
	}

	@SuppressWarnings("unused")
	private void secureUpdateColumn(Object record, String column) {
		U.secure(canUpdateColumn(record, column), "Not enough privileges to update the column: %s!", column);
	}

	private void resetInvisibleColumns(Object record) {
		if (!sudo) {
			Secure.resetInvisibleProperties(username(), record);
		}
	}

	public long getVersionOf(long id) {
		Object ver = readColumn(id, VERSION);
		return ver != null ? Cls.convert(ver, Long.class) : 0;
	}

	private void failIfReadonlyTx() {
		U.must(!data.insideTx.get() || !data.txReadonly.get(), "Cannot modify data inside read-only transaction!");
	}

	public void prefill(Object entity) {
		_insert(entity, false);
	}

}
