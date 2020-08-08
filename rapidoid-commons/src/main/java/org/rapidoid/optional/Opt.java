/*-
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2020 Nikolche Mihajlovski and contributors
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

package org.rapidoid.optional;

import org.rapidoid.RapidoidThing;
import org.rapidoid.u.U;

/**
 * A replacement for JDK8's Optional.
 *
 * @author Nikolche Mihajlovski
 * @since 5.5.1
 */
public final class Opt<E> extends RapidoidThing {

	private final E value;

	private Opt(E value) {
		this.value = value;
	}

	public static <T> Opt<T> of(T value) {
		U.notNull(value, "value");
		return new Opt<>(value);
	}

	public static <T> Opt<T> maybe(T value) {
		return new Opt<>(value);
	}

	public static <T> Opt<T> empty() {
		return new Opt<>(null);
	}

	public boolean exists() {
		return value != null;
	}

	public E get() {
		U.must(exists(), "The optional value doesn't exist!");
		return value;
	}

	public E orFail(String errMsg) {
		if (exists()) {
			return get();
		} else {
			throw U.rte(errMsg);
		}
	}

}
