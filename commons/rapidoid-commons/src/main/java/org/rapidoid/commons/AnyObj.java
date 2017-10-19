package org.rapidoid.commons;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.Collection;
import java.util.List;

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
@Since("4.0.0")
public class AnyObj extends RapidoidThing {

	public static boolean contains(Object arrOrColl, Object value) {
		if (arrOrColl instanceof Object[]) {
			Object[] arr = (Object[]) arrOrColl;
			return Arr.indexOf(arr, value) >= 0;

		} else if (arrOrColl instanceof Collection<?>) {
			Collection<?> coll = (Collection<?>) arrOrColl;
			return coll.contains(value);

		} else if (arrOrColl == null) {
			return false;

		} else {
			return U.eq(arrOrColl, value);
		}
	}

	@SuppressWarnings("unchecked")
	public static Object include(Object arrOrColl, Object item) {
		if (arrOrColl instanceof Object[]) {
			Object[] arr = (Object[]) arrOrColl;
			return Arr.indexOf(arr, item) < 0 ? Msc.expand(arr, item) : arr;
		} else if (arrOrColl instanceof Collection<?>) {
			Collection<Object> coll = (Collection<Object>) arrOrColl;
			if (!coll.contains(item)) {
				coll.add(item);
			}
			return coll;
		} else {
			throw Err.illegalArg("Expected array or collection!");
		}
	}

	@SuppressWarnings("unchecked")
	public static Object exclude(Object arrOrColl, Object item) {
		if (arrOrColl instanceof Object[]) {
			Object[] arr = (Object[]) arrOrColl;
			int ind = Arr.indexOf(arr, item);
			return ind >= 0 ? Msc.deleteAt(arr, ind) : arr;
		} else if (arrOrColl instanceof Collection<?>) {
			Collection<Object> coll = (Collection<Object>) arrOrColl;
			if (coll.contains(item)) {
				coll.remove(item);
			}
			return coll;
		} else {
			throw Err.illegalArg("Expected array or collection!");
		}
	}

	public static Object[] flat(Object... arr) {
		List<Object> flat = U.list();
		flatInsertInto(flat, 0, arr);
		return flat.toArray();
	}

	@SuppressWarnings("unchecked")
	public static <T> int flatInsertInto(List<T> dest, int index, Object item) {
		if (index > dest.size()) {
			index = dest.size();
		}
		int inserted = 0;

		if (item instanceof Object[]) {
			Object[] arr = (Object[]) item;
			for (Object obj : arr) {
				inserted += flatInsertInto(dest, index + inserted, obj);
			}
		} else if (item instanceof Collection<?>) {
			Collection<?> coll = (Collection<?>) item;
			for (Object obj : coll) {
				inserted += flatInsertInto(dest, index + inserted, obj);
			}
		} else if (item != null) {
			if (index >= dest.size()) {
				dest.add((T) item);
			} else {
				dest.add(index + inserted, (T) item);
			}
			inserted++;
		}

		return inserted;
	}

	public static <T, V extends T> List<T> withoutNulls(V... values) {
		List<T> list = U.list();

		for (T val : values) {
			if (val != null) {
				list.add(val);
			}
		}

		return list;
	}

}
