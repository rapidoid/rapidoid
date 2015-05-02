package org.rapidoid.http.client;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.data.Ranges;
import org.rapidoid.lambda.Callback;
import org.rapidoid.util.U;

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

@Authors("Nikolche Mihajlovski")
@Since("2.5.0")
public class HttpClientBodyCallback implements HttpClientCallback {

	private final Callback<String> bodyCallback;

	public HttpClientBodyCallback(Callback<String> bodyCallback) {
		this.bodyCallback = bodyCallback;
	}

	@Override
	public void onResult(Buf buffer, Ranges head, Ranges body) {
		bodyCallback.onDone(body.getConcatenated(buffer.bytes(), 0, body.count - 1, ""), null);
	}

	@Override
	public void onError(String msg) {
		bodyCallback.onDone(null, U.rte(msg));
	}

}
