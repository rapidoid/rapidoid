package org.rapidoid.http;

/*
 * #%L
 * rapidoid-integration-tests
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
import org.rapidoid.crypto.Crypto;
import org.rapidoid.io.IO;
import org.rapidoid.io.Upload;
import org.rapidoid.u.U;

import java.util.List;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class HttpMultipartFormTest extends IsolatedIntegrationTest {

	@Test
	public void shouldHandleUploads() throws Throwable {
		defaultServerSetup();

		String hash1 = Crypto.md5(IO.loadBytes("test1.txt"));
		String hash2 = Crypto.md5(IO.loadBytes("test2.txt"));
		String hash3 = Crypto.md5("");

		Upload file1 = Upload.from("test1.txt");
		Upload file2 = Upload.from("test2.txt");

		Map<String, List<Upload>> files = U.map("f1", U.list(file1), "f2", U.list(file2));

		HttpClient client = HTTP.client().host(LOCALHOST).cookie("foo", "bar");

		String res = client.post(localhost("/upload"))
			.header("Cookie", "COOKIE1=a")
			.data("a", "bb")
			.files(files)
			.fetch();

		eq(res, "bar:a:bb:2:" + U.join(":", hash1, hash2, hash3));

		client.close();
	}

	@Test
	public void shouldHandleBigUploads() throws Throwable {
		defaultServerSetup();

		Map<String, String> cookies = U.map("foo", "bar", "COOKIE1", "a");

		String hash1 = Crypto.md5(IO.loadBytes("test1.txt"));
		String hash2 = Crypto.md5(IO.loadBytes("test2.txt"));
		String hash3 = Crypto.md5(IO.loadBytes("rabbit.jpg"));

		Upload file1 = Upload.from("test1.txt");
		Upload file2 = Upload.from("test2.txt");
		Upload file3 = Upload.from("rabbit.jpg");

		Map<String, List<Upload>> files = U.map("f1", U.list(file1), "f2", U.list(file2), "f3", U.list(file3));

		HttpClient client = HTTP.client().host(LOCALHOST).cookies(cookies);

		String res = client.post(localhost("/upload"))
			.data("a", "d")
			.files(files)
			.fetch();

		eq(res, "bar:a:d:3:" + U.join(":", hash1, hash2, hash3));

		client.close();
	}

}
