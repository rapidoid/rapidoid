package org.rapidoid.integrate;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Metadata;
import org.rapidoid.ioc.Beans;
import org.rapidoid.u.U;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class GuiceBeans implements Beans {

	private final Injector injector;

	public GuiceBeans(Injector injector) {
		this.injector = injector;
	}

	@Override
	public <T> T get(Class<T> type) {
		return injector.getInstance(type);
	}

	@Override
	public Set<Object> getAll() {
		return getBeans(null);
	}

	@Override
	public final Set<Object> getAnnotated(Collection<Class<? extends Annotation>> annotations) {
		return getBeans(annotations);
	}

	private Set<Object> getBeans(Collection<Class<? extends Annotation>> annotations) {
		Set<Object> beans = U.set();

		for (Map.Entry<Key<?>, Binding<?>> e : injector.getAllBindings().entrySet()) {

			Key<?> key = e.getKey();
			Binding<?> value = e.getValue();

			boolean include = false;
			if (U.notEmpty(annotations)) {
				if (key.getTypeLiteral() != null && key.getTypeLiteral().getRawType() != null) {

					Class<?> type = key.getTypeLiteral().getRawType();
					if (Metadata.isAnnotatedAny(type, annotations)) {
						include = true;
					}
				}
			} else {
				include = true;
			}

			if (include) {
				beans.add(value.getProvider().get());
			}
		}

		return beans;
	}


}
