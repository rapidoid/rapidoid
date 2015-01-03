package org.rapidoid.util;

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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.rapidoid.lambda.Mapper;
import org.rapidoid.lambda.Predicate;

public class BeanProperties implements Iterable<Prop> {

	@SuppressWarnings("unchecked")
	public static BeanProperties NONE = new BeanProperties(Collections.EMPTY_MAP);

	public final Map<String, Prop> map;

	public final Collection<Prop> props;

	public final Collection<String> names;

	public final Map<Predicate<Prop>, BeanProperties> selections = U
			.autoExpandingMap(new Mapper<Predicate<Prop>, BeanProperties>() {
				@Override
				public BeanProperties map(Predicate<Prop> filter) throws Exception {
					Map<String, Prop> selected = new LinkedHashMap<String, Prop>();

					for (Entry<String, Prop> e : map.entrySet()) {
						if (U.eval(filter, e.getValue())) {
							selected.put(e.getKey(), e.getValue());
						}
					}

					return new BeanProperties(selected);
				}
			});

	public BeanProperties(Map<String, Prop> properties) {
		this.map = Collections.unmodifiableMap(properties);
		this.props = Collections.unmodifiableCollection(properties.values());
		this.names = Collections.unmodifiableCollection(properties.keySet());
	}

	@Override
	public Iterator<Prop> iterator() {
		return props.iterator();
	}

	public Prop get(String property) {
		return map.get(property);
	}

	public BeanProperties select(Predicate<Prop> filter) {
		return selections.get(filter);
	}

	@Override
	public String toString() {
		return "BeanProperties [map=" + map + ", selections=" + selections + "]";
	}

}
