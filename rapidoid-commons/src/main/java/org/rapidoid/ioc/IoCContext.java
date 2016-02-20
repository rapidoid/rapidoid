package org.rapidoid.ioc;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Mapper;

import java.util.List;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public interface IoCContext {

	void reset();

	void manage(Object... classesOrInstances);

	<T> T singleton(Class<T> type);

	<T> T autowire(T target);

	<T> T autowire(T target, Mapper<String, Object> session, Mapper<String, Object> bindings);

	<T> T inject(T target);

	<T> T inject(T target, Map<String, Object> properties);

	boolean remove(Object bean);

	<K, V> Map<K, V> autoExpandingInjectingMap(Class<V> clazz);

	Object findInstanceOf(String className);

	IoCContextChanges reload(List<Class<?>> modified, List<String> deleted);

	Map<String, Object> info();

}
