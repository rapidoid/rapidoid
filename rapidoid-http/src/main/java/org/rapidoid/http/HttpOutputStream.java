package org.rapidoid.http;

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

import java.io.IOException;
import java.io.OutputStream;

import org.rapidoid.annotation.Authors;

@Authors("Nikolche Mihajlovski")
public class HttpOutputStream extends OutputStream {

	private final OutputStream out;

	private final HttpExchangeImpl x;

	private boolean initialized;

	public HttpOutputStream(HttpExchangeImpl x) {
		this.x = x;
		out = x.output().asOutputStream();
	}

	@Override
	public void write(int n) throws IOException {
		if (!initialized) {
			x.ensureHeadersComplete();
			initialized = true;
		}
		out.write(n);
	}

}
