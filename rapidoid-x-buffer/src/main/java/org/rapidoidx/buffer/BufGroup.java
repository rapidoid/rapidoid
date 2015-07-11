package org.rapidoidx.buffer;

/*
 * #%L
 * rapidoid-x-buffer
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.nio.ByteBuffer;
import java.util.concurrent.Callable;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.pool.Pool;
import org.rapidoid.pool.Pools;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class BufGroup {

	private final int factor;

	private final int capacity;

	private final Pool<ByteBuffer> pool;

	private final boolean synchronizedBuffers;

	public BufGroup(int factor, boolean synchronizedBuffers) {
		this.synchronizedBuffers = synchronizedBuffers;
		this.factor = factor;
		this.capacity = (int) Math.pow(2, factor);

		pool = Pools.create(new Callable<ByteBuffer>() {
			@Override
			public ByteBuffer call() {
				return ByteBuffer.allocateDirect(capacity);
			}
		}, 1000);
	}

	public BufGroup(int factor) {
		this(factor, false);
	}

	public Buf newBuf(String name) {
		Buf buf = new MultiBuf(pool, factor, name);

		if (synchronizedBuffers) {
			buf = new SynchronizedBuf(buf);
		}

		return buf;
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
