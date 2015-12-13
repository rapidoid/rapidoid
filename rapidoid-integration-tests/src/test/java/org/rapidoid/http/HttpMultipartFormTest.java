package org.rapidoid.http;

/*
 * #%L
 * rapidoid-integration-tests
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

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.crypto.Crypto;
import org.rapidoid.io.IO;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class HttpMultipartFormTest extends HttpTestCommons {

	@Test
	public void shouldHandleUploads() throws Throwable {
		defaultServerSetup();

		String hash1 = Crypto.md5(FileUtils.readFileToByteArray(new File(IO.resource("test1.txt").toURI())));
		String hash2 = Crypto.md5(FileUtils.readFileToByteArray(new File(IO.resource("test2.txt").toURI())));
		String hash3 = Crypto.md5("");

		eq(upload("/upload", U.map("a", "bb"), U.map("f1", "test1.txt", "f2", "test2.txt")),
				"bar:a:bb:2:" + U.join(":", hash1, hash2, hash3));

		shutdown();
	}

	@Test
	public void shouldHandleBigUploads() throws Throwable {
		defaultServerSetup();

		String hash1 = Crypto.md5(FileUtils.readFileToByteArray(new File(IO.resource("test1.txt").toURI())));
		String hash2 = Crypto.md5(FileUtils.readFileToByteArray(new File(IO.resource("test2.txt").toURI())));
		String hash3 = Crypto.md5(FileUtils.readFileToByteArray(new File(IO.resource("rabbit.jpg").toURI())));

		eq(upload("/upload", U.map("a", "d"), U.map("f1", "test1.txt", "f2", "test2.txt", "f3", "rabbit.jpg")),
				"bar:a:d:3:" + U.join(":", hash1, hash2, hash3));

		shutdown();
	}

}
