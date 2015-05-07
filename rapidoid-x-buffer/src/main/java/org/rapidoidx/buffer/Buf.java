package org.rapidoidx.buffer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.wrap.IntWrap;
import org.rapidoidx.bytes.Bytes;
import org.rapidoidx.data.Range;
import org.rapidoidx.data.Ranges;

/*
 * #%L
 * rapidoid-x-buffer
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public interface Buf {

	IncompleteReadException INCOMPLETE_READ = new IncompleteReadException();

	byte get(int position);

	void put(int position, byte value);

	void append(byte value);

	void put(int position, byte[] bytes, int offset, int length);

	int size();

	void append(ByteBuffer wrap);

	int append(ReadableByteChannel channel) throws IOException;

	int append(String s);

	void append(byte[] bytes);

	void append(byte[] bytes, int offset, int length);

	String data();

	int writeTo(WritableByteChannel channel) throws IOException;

	int writeTo(ByteBuffer buffer);

	void deleteBefore(int position);

	void deleteAfter(int position);

	void deleteLast(int count);

	int unitCount();

	int unitSize();

	void clear();

	String get(Range range);

	long getN(Range range);

	boolean isSingle();

	ByteBuffer getSingle();

	ByteBuffer first();

	int putNumAsText(int position, long num, boolean forward);

	void get(Range range, byte[] dest, int offset);

	byte next();

	void back(int count);

	byte peek();

	boolean hasRemaining();

	int remaining();

	int position();

	int limit();

	void position(int position);

	void limit(int limit);

	void upto(byte value, Range range);

	ByteBuffer exposed();

	void scanUntil(byte value, Range range);

	void scanWhile(byte value, Range range);

	void skip(int count);

	ByteBuffer bufAt(int index);

	int bufCount();

	int bufferIndexOf(int position);

	int bufferOffsetOf(int position);

	OutputStream asOutputStream();

	String asText();

	Bytes bytes();

	void scanLn(Range range);

	void scanLnLn(Ranges ranges);

	void scanN(int count, Range range);

	String readLn();

	String readN(int count);

	byte[] readNbytes(int count);

	void scanTo(byte sep, Range range, boolean failOnLimit);

	int scanTo(byte sep1, byte sep2, Range range, boolean failOnLimit);

	void scanLnLn(Ranges ranges, IntWrap result, byte end1, byte end2);

}
