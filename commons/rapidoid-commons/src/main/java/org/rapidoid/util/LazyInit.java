package org.rapidoid.util;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Callable;

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
@Since("5.2.4")
public final class LazyInit<T> extends RapidoidThing {

	private final Callable<T> initializer;

	private volatile T initializedValue;

	public LazyInit(Callable<T> initializer) {
		this.initializer = initializer;
	}

	public T get() {
		T value = initializedValue;

		if (value == null) {
			synchronized (this) {
				value = initializedValue;

				if (value == null) {
					initializedValue = initialize();
					value = initializedValue;
				}
			}
		}

		return value;
	}

	private T initialize() {
		try {
			return initializer.call();
		} catch (Exception e) {
			throw new RuntimeException("Lazy initialization error!", e);
		}
	}

	public T reset() {
		T value = initializedValue;

		if (value != null) {
			synchronized (this) {
				value = initializedValue;

				if (value != null) {
					initializedValue = null;
				}
			}
		}

		return value;
	}

	public T resetAndClose() {
		T value = reset();

		if (value != null) {
			if (value instanceof Closeable) {
				close((Closeable) value);
			} else {
				throw new IllegalStateException("Cannot close the lazily initialized value, it's not Closeable!");
			}
		}

		return value;
	}

	private static void close(Closeable closeable) {
		try {
			closeable.close();
		} catch (IOException e) {
			throw new RuntimeException("Error occurred while closing the lazily initialized value!", e);
		}
	}

	public void setValue(T value) {
		this.initializedValue = value;
	}

	public T getValue() {
		return initializedValue;
	}

	public boolean isInitialized() {
		return initializedValue != null;
	}
}
