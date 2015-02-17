package org.rapidoid.db;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.var.Var;

/**
 * Common functionality for both class-based and interface-based persisted domain model entities.
 */
@Authors("Nikolche Mihajlovski")
@Since("2.2.0")
public interface IEntityCommons {

	long id();

	void id(long id);

	long version();

	void version(long version);

	String createdBy();

	void createdBy(String createdBy);

	Date createdOn();

	void createdOn(Date createdOn);

	String lastUpdatedBy();

	void lastUpdatedBy(String updatedBy);

	Date lastUpdatedOn();

	void lastUpdatedOn(Date updatedOn);

	<K, V> ConcurrentMap<K, V> _map(String name);

	<T> List<T> _list(String name);

	<T> Set<T> _set(String name);

	<T> Var<T> _var(String name, T defaultValue);

}
