package org.rapidoid.fluent.dox;

/*
 * #%L
 * rapidoid-fluent
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

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Nikolche Mihajlovski
 * @since 5.0.0
 */
public class DoGroup<T> {

	private final Stream<T> stream;

	public DoGroup(Stream<T> stream) {
		this.stream = stream;
	}

	public <K, V> Map<K, List<T>> by(Function<T, K> transformation) {
		return stream.collect(Collectors.groupingBy(transformation));
	}

}
