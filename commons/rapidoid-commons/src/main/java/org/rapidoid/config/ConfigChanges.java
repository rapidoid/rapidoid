package org.rapidoid.config;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.u.U;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
@Since("5.3.0")
public class ConfigChanges extends RapidoidThing {

	public final boolean initial;

	public final Map<String, Object> added = Coll.synchronizedMap();

	public final Map<String, Object> changed = Coll.synchronizedMap();

	public final Set<String> removed = Coll.synchronizedSet();

	public final List<String> keys = Coll.synchronizedList();

	private ConfigChanges(boolean initial) {
		this.initial = initial;
	}

	public static ConfigChanges from(List<String> keys, Map<String, Object> old, Map<String, Object> fresh, boolean initial) {

		ConfigChanges changes = new ConfigChanges(initial);

		Coll.assign(changes.keys, keys);

		for (Map.Entry<String, Object> e : old.entrySet()) {

			String key = e.getKey();
			Object oldValue = e.getValue();
			Object newValue = fresh.get(key);

			if (!isEmptyValue(oldValue)) {
				if (!isEmptyValue(newValue)) {
					if (U.neq(oldValue, newValue)) {
						changes.changed.put(key, newValue);
					}
				} else {
					changes.removed.add(key);
				}
			}
		}

		for (Map.Entry<String, Object> e : fresh.entrySet()) {

			String key = e.getKey();
			Object oldValue = old.get(key);
			Object newValue = fresh.get(key);

			if (!isEmptyValue(newValue)) {
				if (isEmptyValue(oldValue)) {
					changes.added.put(key, newValue);
				}
			}
		}

		return changes;
	}

	private static boolean isEmptyValue(Object value) {
		return value == null || (value instanceof Map && ((Map) value).isEmpty());
	}

	public int count() {
		return added.size() + changed.size() + removed.size();
	}

	public <T> Map<String, T> getAddedAs(Class<T> type) {
		return Coll.toBeanMap(added, type);
	}

	public <T> Map<String, T> getChangedAs(Class<T> type) {
		return Coll.toBeanMap(changed, type);
	}

	public <T> Map<String, T> getAddedOrChangedAs(Class<T> type) {
		Map<String, Object> addedOrChanged = U.map();

		addedOrChanged.putAll(added);
		addedOrChanged.putAll(changed);

		return Coll.toBeanMap(addedOrChanged, type);
	}

	@Override
	public String toString() {
		return "ConfigChanges{" +
			"initial=" + initial +
			", added=" + added +
			", changed=" + changed +
			", removed=" + removed +
			", keys=" + keys +
			'}';
	}

}
