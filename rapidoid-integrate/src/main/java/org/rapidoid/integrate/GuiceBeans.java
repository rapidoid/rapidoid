package org.rapidoid.integrate;

/*
 * #%L
 * rapidoid-integrate
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import org.rapidoid.RapidoidThing;
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
public class GuiceBeans extends RapidoidThing implements Beans {

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
