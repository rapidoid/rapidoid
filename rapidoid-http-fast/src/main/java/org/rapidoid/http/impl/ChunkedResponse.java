package org.rapidoid.http.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.Once;
import org.rapidoid.writable.ReusableWritable;

import java.io.IOException;
import java.io.OutputStream;

/*
 * #%L
 * rapidoid-http-fast
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
@Since("5.4.0")
public class ChunkedResponse extends OutputStream {

	private final RespImpl resp;

	private final ReusableWritable chunk = new ReusableWritable();

	private volatile Once startChunkedResp = new Once();

	private volatile boolean closed;

	ChunkedResponse(RespImpl resp) {
		this.resp = resp;
	}

	@Override
	public synchronized void write(int b) throws IOException {
		chunk.write(b);
	}

	@Override
	public synchronized void write(byte[] b) throws IOException {
		chunk.write(b);
	}

	@Override
	public synchronized void write(byte[] b, int off, int len) throws IOException {
		chunk.write(b, off, len);
	}

	@Override
	public synchronized void flush() throws IOException {
		// lazy init
		if (startChunkedResp.go()) resp.startChunkedOutputStream();

		// the chunk must not be empty (empty chunk terminates the HTTP response)
		if (chunk.size() > 0) {
			resp.chunk(chunk.array(), 0, chunk.size());
			chunk.reset();
		}
	}

	@Override
	public synchronized void close() throws IOException {
		flush();
		resp.terminatingChunk();
		closed = true;
	}

	public synchronized boolean isClosed() {
		return closed;
	}

}
