package org.rapidoid.buffer;

/*
 * #%L
 * rapidoid-buffer
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import java.nio.ByteBuffer;
import java.util.concurrent.Callable;

import org.rapidoid.pool.ArrayPool;
import org.rapidoid.pool.Pool;

public class BufGroup {

	private final int factor;

	private final int capacity;

	private final Pool<ByteBuffer> pool;

	public BufGroup(int factor) {
		this.factor = factor;
		this.capacity = (int) Math.pow(2, factor);

		pool = new ArrayPool<ByteBuffer>(new Callable<ByteBuffer>() {
			@Override
			public ByteBuffer call() {
				return ByteBuffer.allocateDirect(capacity);
			}
		}, 1000);
	}

	public Buf newBuf(String name) {
		return new MultiBuf(pool, factor, name);
	}

	public Buf newBuf() {
		return newBuf("no-name");
	}

	public Buf from(String s, String name) {
		return from(ByteBuffer.wrap(s.getBytes()), name);
	}

	public Buf from(ByteBuffer bbuf, String name) {
		Buf buf = newBuf(name);
		buf.append(bbuf);
		return buf;
	}

	public int instances() {
		return pool.instances();
	}

}
