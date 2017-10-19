package org.rapidoid.beany;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.collection.Coll;
import org.rapidoid.lambda.Lmbd;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

/*
 * #%L
 * rapidoid-commons
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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class BeanProperties extends RapidoidThing implements Iterable<Prop> {

	@SuppressWarnings("unchecked")
	public static BeanProperties NONE = new BeanProperties(Collections.EMPTY_MAP);

	public final Map<String, Prop> map;

	public final Map<String, Prop> lowercaseMap;

	public final List<Prop> props;

	public final List<String> names;

	public final ConcurrentMap<String, Object> extras = Coll.concurrentMap();

	public final Map<PropertySelector, BeanProperties> selections = Coll
		.autoExpandingMap(new Mapper<PropertySelector, BeanProperties>() {
			@Override
			public BeanProperties map(PropertySelector selector) throws Exception {

				Set<String> include = selector.include();
				Set<String> exclude = selector.exclude();

				List<Prop> selected = new ArrayList<Prop>(10);

				for (String propName : include) {
					Prop prop = map.get(propName);

					if (prop == null) {
						if (JSProp.is(propName)) {
							prop = new JSProp(propName);

						} else if (ActionsProp.is(propName)) {
							prop = new ActionsProp();

						} else {
							throw U.rte("Cannot find property '%s'!", propName);
						}
					}

					if (!veto(prop) && Lmbd.eval(selector, prop) && Lmbd.eval(selector, prop)) {
						selected.add(prop);
					}
				}

				if (U.notEmpty(exclude) || U.isEmpty(include)) {
					for (Prop prop : map.values()) {
						if (!veto(prop) && !selected.contains(prop) && !exclude.contains(prop.getName()) && Lmbd.eval(selector, prop)) {
							selected.add(prop);
						}
					}
				}

				if (U.isEmpty(include)) {
					Collections.sort(selected, selector);
				}

				return from(selected);
			}
		});

	private boolean veto(Prop prop) {
		return prop.getName().equalsIgnoreCase("clone");
	}

	public BeanProperties(Map<String, ? extends Prop> properties) {
		this.map = Collections.unmodifiableMap(properties);
		this.lowercaseMap = Msc.lowercase(map);
		this.props = Collections.unmodifiableList(U.list(properties.values()));
		this.names = Collections.unmodifiableList(U.list(properties.keySet()));
	}

	@Override
	public Iterator<Prop> iterator() {
		return props.iterator();
	}

	public Prop get(String property) {
		Prop prop = map.get(property);

		if (prop == null) {
			prop = lowercaseMap.get(property.toLowerCase());
		}

		return prop;
	}

	public BeanProperties select(PropertySelector selector) {
		return selections.get(selector);
	}

	public BeanProperties annotated(Class<? extends Annotation> annotated) {
		return select(new AnnotatedPropertyFilter(annotated));
	}

	@Override
	public String toString() {
		return "BeanProperties [map=" + map + ", selections=" + selections + "]";
	}

	public static BeanProperties from(List<Prop> properties) {
		Map<String, Prop> map = Coll.synchronizedMap();

		for (Prop prop : properties) {
			map.put(prop.getName(), prop);
		}

		return new BeanProperties(map);
	}

	public static BeanProperties from(Map<String, ?> map) {
		Map<String, Prop> properties = Coll.synchronizedMap();

		for (Entry<?, ?> e : map.entrySet()) {
			Object key = e.getKey();
			Prop prop = new MapProp(String.valueOf(key), key, Cls.of(e.getValue()));
			properties.put(prop.getName(), prop);
		}

		return new BeanProperties(properties);
	}

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

}
