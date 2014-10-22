package org.rapidoid.db;

import java.util.List;

import org.rapidoid.lambda.Predicate;
import org.rapidoid.lambda.V1;

public interface Db {

	long insert(Object record);

	<E> E get(long id);

	<E> E get(long id, Class<E> clazz);

	void update(long id, Object record);

	void delete(long id);

	<T> T read(long id, String column);

	<E> List<E> find(Predicate<E> match);

	<E> void each(V1<E> lambda);

	void transaction(Runnable transaction);

}