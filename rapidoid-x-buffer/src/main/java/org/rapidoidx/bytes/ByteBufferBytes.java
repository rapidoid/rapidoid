package org.rapidoidx.bytes;

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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class ByteBufferBytes implements Bytes {

	private ByteBuffer buf;

	public ByteBufferBytes() {}

	public ByteBufferBytes(ByteBuffer buf) {
		this.buf = buf;
	}

	@Override
	public byte get(int position) {
		return buf.get(position);
	}

	@Override
	public int limit() {
		return buf.limit();
	}

	public void setBuf(ByteBuffer buf) {
		this.buf = buf;
	}

}
