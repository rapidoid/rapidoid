package org.rapidoid.lambda;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.U;

/*
 * #%L
 * rapidoid-utils
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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
@Since("3.0.0")
public class ResultCounterCallback<T> implements Callback<T> {

	// FIXME refactor the built-in callbacks using decorators

	@SuppressWarnings("unchecked")
	private final Set<T> results = Collections.synchronizedSet(U.<T> set());

	private final AtomicLong resultsN = new AtomicLong();

	@Override
	public void onDone(T result, Throwable error) {
		if (error == null) {
			results.add(result);
			resultsN.incrementAndGet();
		}
	}

	public long getResultCount() {
		return resultsN.get();
	}

	public Set<T> getResults() {
		return results;
	}

}
