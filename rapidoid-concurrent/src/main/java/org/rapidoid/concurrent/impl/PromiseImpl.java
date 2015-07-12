package org.rapidoid.concurrent.impl;

/*
 * #%L
 * rapidoid-concurrent
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

import java.util.concurrent.TimeoutException;

import org.rapidoid.concurrent.Promise;
import org.rapidoid.util.U;

/**
 * @author Nikolche Mihajlovski
 * @since 4.1.0
 */
public class PromiseImpl<T> implements Promise<T> {

	private volatile boolean done;

	private volatile T result;

	private volatile Throwable error;

	public void setResult(T result) {
		this.result = result;
		done = true;
	}

	public void setError(Throwable error) {
		this.error = error;
		done = true;
	}

	@Override
	public void onDone(T result, Throwable error) {
		if (error != null) {
			setError(error);
		} else {
			setResult(result);
		}
	}

	public T get() {
		try {
			return get(Long.MAX_VALUE);
		} catch (TimeoutException e) {
			throw U.notExpected();
		}
	}

	@Override
	public T get(long timeoutMs) throws TimeoutException {
		return get(timeoutMs, 5);
	}

	@Override
	public T get(long timeoutMs, long sleepingIntervalMs) throws TimeoutException {
		long waitingSince = U.time();

		while (!done) {
			if (U.time() - waitingSince > timeoutMs) {
				throw new TimeoutException();
			}

			U.sleep(sleepingIntervalMs);
		}

		if (error != null) {
			throw U.rte("Cannot get the result, there was an error!", error);
		}

		return result;
	}

	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public boolean isSuccessful() {
		U.must(done, "The promise is not done yet!");
		return error == null;
	}

}
