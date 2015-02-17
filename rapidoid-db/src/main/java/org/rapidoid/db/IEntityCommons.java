package org.rapidoid.db;

import java.util.Date;
import java.util.List;
import java.util.Map;
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

	<K, V> ConcurrentMap<K, V> _map(Object key);

	<T> List<T> _list(Object key);

	<T> Set<T> _set(Object key);

	<T> Var<T> _var(Object key, T defaultValue);

	<T> T _extra(Object key);

	void _extra(Object key, Object value);

	Map<Object, Object> _extras();

	<T> T _tmp(Object key);

	void _tmp(Object key, Object value);

	Map<Object, Object> _tmps();

}
