package org.rapidoid.fluent.utils;

import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;

/*
 * #%L
 * rapidoid-fluent
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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
public class StreamUtils {

	/**
	 * Based on http://stackoverflow.com/questions/27547519/most-efficient-way-to-get-the-last-element-of-a-stream
	 */
	public static <T> Optional<T> findLastOf(Stream<T> stream) {
		Spliterator<T> split = stream.spliterator();

		if (split.hasCharacteristics(Spliterator.SIZED | Spliterator.SUBSIZED)) {
			while (true) {
				Spliterator<T> part = split.trySplit();

				if (part == null) {
					break;
				}

				if (split.getExactSizeIfKnown() == 0) {
					split = part;
					break;
				}
			}
		}

		T value = null;
		for (Iterator<T> it = traverse(split); it.hasNext(); ) {
			value = it.next();
		}

		return Optional.ofNullable(value);
	}

	private static <T> Iterator<T> traverse(Spliterator<T> sp) {
		Spliterator<T> prev = sp.trySplit();

		if (prev == null) {
			return Spliterators.iterator(sp);
		}

		Iterator<T> it = traverse(sp);

		if (it != null && it.hasNext()) {
			return it;
		}

		return traverse(prev);
	}

}
