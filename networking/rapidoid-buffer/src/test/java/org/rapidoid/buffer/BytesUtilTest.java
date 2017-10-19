package org.rapidoid.buffer;

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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.bytes.BytesUtil;
import org.rapidoid.data.BufRange;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class BytesUtilTest extends BufferTestCommons {

	@Test
	public void testValidURIs() {
		isTrue(isValid("/"));
		isTrue(isValid("/a"));
		isTrue(isValid("/a/"));
		isTrue(isValid("/abcd/"));
		isTrue(isValid("/abc.tar.gz"));
		isTrue(isValid("/abc.js/"));
		isTrue(isValid("/xx%34%64/"));
		isTrue(isValid("/abc-=-=_=+fg/"));
		isTrue(isValid("/-=AaaaA!0!!AZazf=__/_.-=-.=_=.+/fg01.++--2AaaAa34.56789/"));
		isTrue(isValid("/.ff"));
		isTrue(isValid("/.some-private/"));
		isTrue(isValid("/ok/./x"));
		isTrue(isValid("/ok/././xyz.abc"));
		isTrue(isValid("/."));
		isTrue(isValid("/?/../"));
		isTrue(isValid("/?ff/../"));
		isTrue(isValid("/?/../"));
		isTrue(isValid("/abc?//"));
		isTrue(isValid("/x?."));
		isTrue(isValid("/a/b/c?x=..&y=3d"));
		isTrue(isValid("/?x=../..////...."));
		isTrue(isValid("/?../..////...."));
		isTrue(isValid("/a:b:c"));
		isTrue(isValid("/z2-._:/#[]@!$&()'*+,;=%01"));
		isTrue(isValid("/z?2-._:/#[]@!$&()'*+,;=%01"));
		isTrue(isValid("/document/?uri=http://user:password@example.com/?foo=bar"));
	}

	@Test
	public void testInalidURIs() {
		isFalse(isValid(""));
		isFalse(isValid("//"));
		isFalse(isValid("./"));
		isFalse(isValid(".."));
		isFalse(isValid("f"));
		isFalse(isValid("."));
		isFalse(isValid("sdfgdfg"));
		isFalse(isValid("/Дфг"));
		isFalse(isValid("/ok/../x"));
		isFalse(isValid("/ok/../../xyz.abc"));
		isFalse(isValid("/f\\b"));
		isFalse(isValid("/f\\bg"));
		isFalse(isValid("/f\ng"));
		isFalse(isValid("/f\rg"));
		isFalse(isValid("/f\tg"));
		isFalse(isValid("/\ng"));
		isFalse(isValid("\ng"));
	}

	private boolean isValid(String uri) {
		Buf buf = buf(uri);
		BufRange uriRange = BufRange.fromTo(0, buf.size());
		return BytesUtil.isValidURI(buf.bytes(), uriRange);
	}

	@Test
	public void testBufferBytes() {
		Buf buf = buf("abc");

		eq(buf.get(0), 'a');
		eq(buf.bytes().get(0), 'a');

		eq(buf.limit(), 3);
		eq(buf.bytes().limit(), 3);

		buf.deleteBefore(1);

		eq(buf.get(0), 'b');
		eq(buf.bytes().get(0), 'b');

		eq(buf.limit(), 2);
		eq(buf.bytes().limit(), 2);

		buf.deleteBefore(1);

		eq(buf.get(0), 'c');
		eq(buf.bytes().get(0), 'c');

		eq(buf.limit(), 1);
		eq(buf.bytes().limit(), 1);

		buf.deleteBefore(1);

		eq(buf.limit(), 0);
		eq(buf.bytes().limit(), 0);
	}

}
