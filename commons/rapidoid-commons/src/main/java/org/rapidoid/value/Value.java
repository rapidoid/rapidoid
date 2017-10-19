package org.rapidoid.value;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

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
public interface Value<T> {

	T get();

	Value<T> orElse(Value<T> alternative);

	<K> K or(K alternative);

	<K> Value<K> to(Class<K> type);

	void set(T value);

	boolean exists();

	Value<String> str();

	List<String> list();

	Value<Long> num();

	Value<Boolean> bool();

	T getOrNull();

	String desc();
}
