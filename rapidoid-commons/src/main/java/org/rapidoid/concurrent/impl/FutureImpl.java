package org.rapidoid.concurrent.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.commons.Err;
import org.rapidoid.concurrent.Future;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.concurrent.TimeoutException;

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

/**
 * @author Nikolche Mihajlovski
 * @since 4.1.0
 */
public class FutureImpl<T> extends RapidoidThing implements Future<T> {

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

	public T get() {
		try {
			return get(Long.MAX_VALUE);
		} catch (TimeoutException e) {
			throw Err.notExpected();
		}
	}

	@Override
	public T get(long timeoutMs) throws TimeoutException {
		return get(timeoutMs, 5);
	}

	@Override
	public T get(long timeoutMs, long sleepingIntervalMs) throws TimeoutException {
		long waitingSince = U.time();

		while (!isDone()) {
			if (Msc.timedOut(waitingSince, timeoutMs)) {
				throw new TimeoutException();
			}

			U.sleep(sleepingIntervalMs);
		}

		if (getError() != null) {
			throw U.rte("Cannot get the result, there was an error!", error);
		}

		return result;
	}

	public Throwable getError() {
		return error;
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
