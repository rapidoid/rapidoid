package org.rapidoid.fluent;

import org.rapidoid.fluent.find.*;

import java.util.Map;
import java.util.stream.Stream;

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

/**
 * @author Nikolche Mihajlovski
 * @since 5.0.0
 */
public class Find {

	public static <T> FindFirst<T> firstOf(Iterable<T> items) {
		return new FindFirst<T>(Do.stream(items));
	}

	public static <K, V> FindFirstBi<K, V> firstOf(Map<K, V> items) {
		return new FindFirstBi<K, V>(items);
	}

	public static <T> FindFirst<T> firstOf(Stream<T> stream) {
		return new FindFirst<T>(Do.stream(stream));
	}

	public static <T> FindAll<T> allOf(Iterable<T> items) {
		return new FindAll<T>(Do.stream(items));
	}

	public static <K, V> FindAllBi<K, V> allOf(Map<K, V> items) {
		return new FindAllBi<K, V>(items);
	}

	public static <T> FindAll<T> allOf(Stream<T> stream) {
		return new FindAll<T>(Do.stream(stream));
	}

	public static <T> FindAny<T> anyOf(Iterable<T> items) {
		return new FindAny<T>(Do.stream(items));
	}

	public static <K, V> FindAnyBi<K, V> anyOf(Map<K, V> items) {
		return new FindAnyBi<K, V>(items);
	}

	public static <T> FindAny<T> anyOf(Stream<T> stream) {
		return new FindAny<T>(Do.stream(stream));
	}

	public static <T> FindLast<T> lastOf(Iterable<T> items) {
		return new FindLast<T>(Do.stream(items));
	}

	public static <K, V> FindLastBi<K, V> lastOf(Map<K, V> items) {
		return new FindLastBi<K, V>(items);
	}

	public static <T> FindLast<T> lastOf(Stream<T> stream) {
		return new FindLast<T>(Do.stream(stream));
	}

	public static <T> FindIn<T> in(Iterable<T> items) {
		return new FindIn<T>(Do.stream(items));
	}

	public static <K, V> FindInBi<K, V> in(Map<K, V> items) {
		return new FindInBi<K, V>(items);
	}

	public static <T> FindIn<T> in(Stream<T> stream) {
		return new FindIn<T>(Do.stream(stream));
	}

}
