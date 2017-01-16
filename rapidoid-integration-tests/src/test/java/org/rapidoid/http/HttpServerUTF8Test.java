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

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class HttpServerUTF8Test extends IsolatedIntegrationTest {

	@Test
	public void shouldHandleUTF8() {
		defaultServerSetup();

		System.out.println("file.encoding = " + System.getProperty("file.encoding"));
		System.out.println("Charset.defaultCharset() = " + Charset.defaultCharset());
		System.out.println("default writer.encoding = "
			+ new OutputStreamWriter(new ByteArrayOutputStream()).getEncoding());

		String message = "ažфbдšгcč";
		System.out.println("UTF-8 message = " + message);
		System.out.println("UTF-8 message length = " + message.length());

		eq(message.length(), 9);

		eq(get("/x?" + message), "GET:/x:" + message);
		eq(get("/echo?" + message), "GET:/echo:" + message);
		eq(get("/echo/abc?" + message), "GET:/echo/abc:" + message);
		eq(get("/echo/abc/d" + message), "GET:/echo/abc/d" + message + ":");
	}

}
