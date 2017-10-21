package org.rapidoid.value;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.Err;
import org.rapidoid.u.U;

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
@Since("5.1.0")
public abstract class AbstractValue<T> extends RapidoidThing implements Value<T> {

	protected abstract T retrieve();

	protected void store(T value) {
		throw Err.notSupported();
	}

	@Override
	public T get() {
		T value = getOrNull();
		U.must(value != null, "The value of %s is mandatory!", U.or(desc(), "the variable"));
		return value;
	}

	@Override
	public T getOrNull() {
		return retrieve();
	}

	@Override
	public Value<T> orElse(Value<T> alternative) {
		U.notNull(alternative, "alternative");
		return new OrValue<T>(this, alternative);
	}

	@Override
	public <K> K or(K alternative) {
		U.notNull(alternative, "alternative");
		T value = getOrNull();
		return value != null ? (K) Cls.convert(value, alternative.getClass()) : alternative;
	}

	@Override
	public <K> Value<K> to(Class<K> type) {
		U.notNull(type, "type");
		return new ToValue<K>(this, type);
	}

	@Override
	public void set(T value) {
		store(value);
	}

	@Override
	public String toString() {
		return U.str(getOrNull());
	}

	@Override
	public boolean exists() {
		return getOrNull() != null;
	}

	@Override
	public Value<String> str() {
		return to(String.class);
	}

	@Override
	public Value<Long> num() {
		return to(long.class);
	}

	@Override
	public Value<Boolean> bool() {
		return to(boolean.class);
	}

	@Override
	public List<String> list() {
		String s = str().getOrNull();
		return s != null ? U.list(s.split(",")) : U.<String>list();
	}

	@Override
	public String desc() {
		return null;
	}


}
