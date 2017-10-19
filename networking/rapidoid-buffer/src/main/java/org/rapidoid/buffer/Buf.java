package org.rapidoid.buffer;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.bytes.Bytes;
import org.rapidoid.data.BufRange;
import org.rapidoid.data.BufRanges;
import org.rapidoid.writable.Writable;
import org.rapidoid.wrap.IntWrap;

import javax.net.ssl.SSLEngine;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/*
 * #%L
 * rapidoid-buffer
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
@Since("2.0.0")
public interface Buf extends Writable {

	IncompleteReadException INCOMPLETE_READ = new IncompleteReadException();

	byte get(int position);

	void put(int position, byte value);

	void append(byte value);

	void put(int position, byte[] bytes, int offset, int length);

	int size();

	void append(ByteBuffer src);

	int append(ReadableByteChannel channel) throws IOException;

	int append(String s);

	void append(byte[] bytes);

	void append(byte[] bytes, int offset, int length);

	void append(ByteArrayOutputStream src);

	String data();

	int writeTo(WritableByteChannel channel) throws IOException;

	int writeTo(WritableByteChannel channel, int srcOffset, int length) throws IOException;

	int writeTo(ByteBuffer buffer);

	int writeTo(ByteBuffer buffer, int srcOffset, int length);

	void deleteBefore(int position);

	void deleteAfter(int position);

	void deleteLast(int count);

	int unitCount();

	int unitSize();

	void clear();

	String get(BufRange range);

	long getN(BufRange range);

	boolean isSingle();

	ByteBuffer getSingle();

	ByteBuffer first();

	int putNumAsText(int position, long num, boolean forward);

	void get(BufRange range, byte[] dest, int offset);

	byte next();

	void back(int count);

	byte peek();

	boolean hasRemaining();

	int remaining();

	int position();

	int limit();

	void position(int position);

	void limit(int limit);

	void upto(byte value, BufRange range);

	ByteBuffer exposed();

	void scanUntil(byte value, BufRange range);

	void scanWhile(byte value, BufRange range);

	void skip(int count);

	ByteBuffer bufAt(int index);

	int bufCount();

	int bufferIndexOf(int position);

	int bufferOffsetOf(int position);

	OutputStream asOutputStream();

	String asText();

	Bytes bytes();

	void scanLn(BufRange range);

	void scanLnLn(BufRanges ranges);

	void scanN(int count, BufRange range);

	String readLn();

	String readN(int count);

	byte[] readNbytes(int count);

	void scanTo(byte sep, BufRange range, boolean failOnLimit);

	int scanTo(byte sep1, byte sep2, BufRange range, boolean failOnLimit);

	void scanLnLn(BufRanges ranges, IntWrap result, byte end1, byte end2);

	void setReadOnly(boolean readOnly);

	int checkpoint();

	void checkpoint(int checkpoint);

	void write(int byteValue) throws IOException;

	void write(byte[] src, int offset, int length) throws IOException;

	Buf unwrap();

	int sslWrap(SSLEngine engine, Buf dest);

}
