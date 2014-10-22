package org.rapidoid.db;

import java.util.List;

import org.rapidoid.lambda.Predicate;

public interface Db {

	long insert(Object record);

	void delete(long id);

	<E> E get(long id, Class<E> clazz);

	void update(long id, Object record);

	<T> T read(long id, String column);

	<E> List<E> find(Predicate<E> match);

	void transaction(Runnable transaction);

}