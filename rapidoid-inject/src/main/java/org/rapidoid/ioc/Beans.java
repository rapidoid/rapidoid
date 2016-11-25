package org.rapidoid.ioc;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;

/**
 * A collection of beans, usually inside a dependency injection context.
 */
@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public interface Beans {

	<T> T get(Class<T> type);

	Set<Object> getAll();

	Set<Object> getAnnotated(Collection<Class<? extends Annotation>> annotations);

}
