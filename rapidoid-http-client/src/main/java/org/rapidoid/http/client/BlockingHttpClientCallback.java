/*-
 * #%L
 * rapidoid-http-client
 * %%
 * Copyright (C) 2014 - 2020 Nikolche Mihajlovski and contributors
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

package org.rapidoid.http.client;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.concurrent.Callbacks;
import org.rapidoid.concurrent.Promise;
import org.rapidoid.concurrent.Promises;
import org.rapidoid.data.BufRange;
import org.rapidoid.data.BufRanges;

@Authors("Nikolche Mihajlovski")
@Since("5.5.0")
public class BlockingHttpClientCallback extends RapidoidThing implements HttpClientCallback {

	private final Promise<byte[]> promise = Promises.create();

	@Override
	public void onResult(Buf buffer, BufRanges head, BufRanges body) {
		BufRange whole = new BufRange();
		whole.start = head.ranges[0].start;
		whole.length = body.last().start + body.last().length;

		byte[] result = whole.bytes(buffer);

		Callbacks.done(promise, result, null);
	}

	@Override
	public void onError(Throwable error) {
		Callbacks.done(promise, null, error);
	}

	public byte[] getResponse() {
		return promise.get();
	}

}
