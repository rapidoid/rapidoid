package org.rapidoid.prop;

/*
 * #%L
 * rapidoid-utils
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import org.rapidoid.lambda.Mapper;
import org.rapidoid.util.U;

public class BeanProperties implements Iterable<Prop> {

	@SuppressWarnings("unchecked")
	public static BeanProperties NONE = new BeanProperties(Collections.EMPTY_MAP);

	public final Map<String, Prop> map;

	public final List<Prop> props;

	public final List<String> names;

	public final ConcurrentMap<String, Object> extras = U.concurrentMap();

	public final Map<PropertySelector, BeanProperties> selections = U
			.autoExpandingMap(new Mapper<PropertySelector, BeanProperties>() {
				@Override
				public BeanProperties map(PropertySelector selector) throws Exception {

					String[] propertyNames = selector.requiredProperties();
					List<Prop> selected = new ArrayList<Prop>(20);

					if (!U.isEmpty(propertyNames)) {
						for (String propName : propertyNames) {
							Prop prop = map.get(propName);
							U.must(prop != null, "Cannot find property '%s'!", propName);
							if (U.eval(selector, prop)) {
								selected.add(prop);
							}
						}
					} else {
						for (Entry<String, Prop> e : map.entrySet()) {
							if (U.eval(selector, e.getValue())) {
								selected.add(e.getValue());
							}
						}
					}

					Collections.sort(selected, selector);

					Map<String, Prop> map = new LinkedHashMap<String, Prop>();
					for (Prop prop : selected) {
						map.put(prop.getName(), prop);
					}
					return new BeanProperties(map);
				}
			});

	public BeanProperties(Map<String, Prop> properties) {
		this.map = Collections.unmodifiableMap(properties);
		this.props = Collections.unmodifiableList(new ArrayList<Prop>(properties.values()));
		this.names = Collections.unmodifiableList(new ArrayList<String>(properties.keySet()));
	}

	@Override
	public Iterator<Prop> iterator() {
		return props.iterator();
	}

	public Prop get(String property) {
		return map.get(property);
	}

	public BeanProperties select(PropertySelector selector) {
		return selections.get(selector);
	}

	@Override
	public String toString() {
		return "BeanProperties [map=" + map + ", selections=" + selections + "]";
	}

}
