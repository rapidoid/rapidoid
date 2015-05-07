package org.rapidoidx.db.impl.inmem;

/*
 * #%L
 * rapidoid-x-db-inmem
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rapidoid.activity.NamedActivity;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.lambda.Callback;
import org.rapidoid.lambda.Operation;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.util.U;
import org.rapidoidx.db.Database;
import org.rapidoidx.db.DbColumn;
import org.rapidoidx.db.DbList;
import org.rapidoidx.db.DbRef;
import org.rapidoidx.db.DbSchema;
import org.rapidoidx.db.DbSet;
import org.rapidoidx.db.impl.DbColumnImpl;
import org.rapidoidx.db.impl.DbSchemaImpl;
import org.rapidoidx.inmem.InMem;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DbImpl extends NamedActivity<Database> implements Database, Serializable {

	private static final long serialVersionUID = 8806801474242822143L;

	@SuppressWarnings("unchecked")
	private static final Set<Class<?>> REL_CLASSES = U.set(DbList.class, DbSet.class, DbRef.class);

	private final DbEntityConstructor constructor = new DbEntityConstructor(this);

	private final InMem inmem;

	private final DbSchema schema;

	public DbImpl(String name, String filename) {
		super(name);
		this.inmem = new InMem(filename, new JacksonEntitySerializer(this), constructor, REL_CLASSES, null);
		this.schema = new DbSchemaImpl();
	}

	public DbImpl(String name, InMem inmem, DbSchema schema) {
		super(name);
		this.inmem = inmem;
		this.schema = schema;
	}

	@Override
	public void loadAndStart() {
		inmem.initAndLoad();
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
	public void delete(Object record) {
		inmem.delete(record);
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
	public <E> void refresh(E record) {
		inmem.refresh(record);
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
	public <T> T readColumn(long id, String column) {
		return inmem.readColumn(id, column);
	}

	@Override
	public <E> List<E> getAll(Class<E> clazz) {
		return inmem.getAll(clazz);
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
	public <E> List<E> find(Iterable<Long> ids) {
		return inmem.find(ids);
	}

	@Override
	public <E> List<E> find(Class<E> clazz, Predicate<E> match, Comparator<E> orderBy) {
		return inmem.find(clazz, match, orderBy);
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
	public Database start() {
		inmem.start();
		return this;
	}

	@Override
	public Database halt() {
		inmem.halt();
		return this;
	}

	@Override
	public Database shutdown() {
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
	public void clear() {
		inmem.clear();
	}

	@Override
	public String toString() {
		return "DB:" + name + "(" + inmem + ")";
	}

	@Override
	public long getIdOf(Object record) {
		return U.or(Beany.getIdIfExists(record), -1L);
	}

	@Override
	public long getVersionOf(long id) {
		return inmem.getVersionOf(id);
	}

	@Override
	public <E> DbColumn<E> column(Map<String, Object> map, String name, Class<E> type) {
		return new DbColumnImpl<E>(map, name, type);
	}

	@Override
	public <E> DbList<E> list(Object holder, String relation) {
		return new InMemDbList<E>(this, holder, relation);
	}

	@Override
	public <E> DbSet<E> set(Object holder, String relation) {
		return new InMemDbSet<E>(this, holder, relation);
	}

	@Override
	public <E> DbRef<E> ref(Object holder, String relation) {
		return new InMemDbRef<E>(this, holder, relation);
	}

	@Override
	public DbSchema schema() {
		return schema;
	}

	@Override
	public Database as(String username) {
		return new DbImpl(name, inmem.as(username), schema);
	}

	@Override
	public Database sudo() {
		return new DbImpl(name, inmem.sudo(), schema);
	}

	public byte[] export() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		inmem.saveTo(output);
		return output.toByteArray();
	}

	public void load(byte[] data) {
		ByteArrayInputStream input = new ByteArrayInputStream(data);
		inmem.loadFrom(input);
	}

	@Override
	public void init(String data, Object... args) {
		Object entity = entity(data, args);
		inmem.sudo().prefill(entity);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> E entity(String rql, Object... args) {
		return (E) schema().entity(rql, args);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <RESULT> RESULT rql(String rql, Object... args) {

		int p = rql.indexOf(' ');
		U.must(p > 0, "Invalid RQL syntax!");

		String cmd = rql.substring(0, p).trim();
		String data = rql.substring(p + 1).trim();

		if (cmd.equalsIgnoreCase("INSERT")) {
			return (RESULT) new Long(insert(entity(data, args)));
		} else {
			throw U.rte("Unknown RQL command: '%s'!", cmd);
		}
	}

}
