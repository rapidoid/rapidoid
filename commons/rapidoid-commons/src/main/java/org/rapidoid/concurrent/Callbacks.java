package org.rapidoid.concurrent;

import org.rapidoid.RapidoidThing;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.log.Log;

import java.util.concurrent.CountDownLatch;

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
public class Callbacks extends RapidoidThing {

	public static <T> void done(Callback<T> callback, T result, Throwable error) {
		if (callback != null) {
			try {
				callback.onDone(result, error);
			} catch (Exception e) {
				Log.error("Callback error", e);
			}
		}
	}

	public static <T> void success(Callback<T> callback, T result) {
		done(callback, result, null);
	}

	public static <T> void error(Callback<T> callback, Throwable error) {
		done(callback, null, error);
	}

	public static <FROM, TO> Callback<FROM> mapping(final Callback<TO> callback, final Mapper<FROM, TO> mapper) {
		return new Callback<FROM>() {

			@Override
			public void onDone(FROM result, Throwable error) throws Exception {
				TO mapped = error == null ? mapper.map(result) : null;
				Callbacks.done(callback, mapped, error);
			}

		};
	}

	public static <T> Callback<T> countDown(final CountDownLatch latch) {
		return new Callback<T>() {
			@Override
			public void onDone(T result, Throwable error) throws Exception {
				latch.countDown();
			}
		};
	}

}
