package org.rapidoid.buffer;

/*
 * #%L
 * rapidoid-core
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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.rapidoid.data.Range;

public interface Buf {

	byte get(int position);

	void put(int position, byte value);

	void append(byte value);

	void put(int position, byte[] bytes, int offset, int length);

	int size();

	void setSource(Buf buf, ByteBuffer src, int offset, int size);

	boolean match(int start, byte[] match, int offset, int length,
			boolean caseSensitive);

	int find(int start, int limit, byte[] match, int offset, int length,
			boolean caseSensitive);

	int find(int start, int limit, byte match, boolean caseSensitive);

	int find(int start, int limit, byte[] match, boolean caseSensitive);

	boolean matches(Range target, byte[] match, boolean caseSensitive);

	boolean startsWith(Range target, byte[] match, boolean caseSensitive);

	void trim(Range target);

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

	void putNumAsText(int position, long num);

	void get(Range range, byte[] dest, int offset);

	void scanTo(byte sep, Range range, boolean failOnLimit);

	void scanTo(byte[] sep, Range range, boolean failOnLimit);

	int scanTo(byte sep1, byte sep2, Range range, boolean failOnLimit);

	int scanTo(byte[] sep1, byte[] sep2, Range range, boolean failOnLimit);

	void scanLn(Range range, boolean failOnLimit);

	void scanN(int len, Range range);

	byte next();

	void back(int count);

	byte peek();

	boolean hasRemaining();

	int remaining();

	String readLn();

	String readN(int count);

	int position();

	int limit();

	void position(int position);

	void limit(int limit);

	void upto(byte value, Range range);

	ByteBuffer exposed();

	void scanUntil(byte value, Range range, boolean failOnLimit);

	void scanWhile(byte value, Range range, boolean failOnLimit);

	void skip(int count);

	int scanLnLn(Range[] ranges);

}
