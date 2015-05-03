package org.rapidoid.http.client;

/*
 * #%L
 * rapidoid-http
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
import org.rapidoid.buffer.Buf;
import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.lambda.ResultOrError;

@Authors("Nikolche Mihajlovski")
@Since("2.5.0")
public class BlockingHttpClientCallback implements HttpClientCallback {

	private final ResultOrError<byte[]> resultOrError = new ResultOrError<byte[]>();

	@Override
	public void onResult(Buf buffer, Ranges head, Ranges body) {
		Range whole = new Range();
		whole.start = head.ranges[0].start;
		whole.length = body.last().start + body.last().length;
		resultOrError.setResult(whole.bytes(buffer));
	}

	@Override
	public void onError(Throwable error) {
		resultOrError.setError(error);
	}

	public byte[] getResponse() {
		return resultOrError.get();
	}

}
