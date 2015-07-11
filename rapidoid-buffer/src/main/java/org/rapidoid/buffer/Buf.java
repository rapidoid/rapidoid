package org.rapidoid.buffer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.bytes.Bytes;
import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.wrap.IntWrap;

/*
 * #%L
 * rapidoid-buffer
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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
@Since("2.0.0")
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

	void setReadOnly(boolean readOnly);

}
