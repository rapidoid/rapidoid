package org.rapidoid.lambda;

/*
 * #%L
 * rapidoid-utils
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class ResultOrError<T> implements Callback<T> {

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
		while (!done) {
			UTILS.sleep(10);
		}

		if (error != null) {
			throw U.rte("Cannot get result, there was an error!", error);
		}

		return result;
	}

}
