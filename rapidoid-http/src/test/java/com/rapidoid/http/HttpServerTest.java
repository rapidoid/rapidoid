package com.rapidoid.http;

/*
 * #%L
 * rapidoid-http
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

import org.testng.annotations.Test;

public class HttpServerTest extends HttpTestCommons {

	@Test
	public void shouldHandleHttpRequests() {
		defaultServerSetup();

		String message = "ažфbдšгcč";

		eq(get("/x?" + message), "GET:/x:/x:" + message);
		eq(get("/echo?" + message), "GET:/echo::" + message);
		eq(get("/echo/abc?" + message), "GET:/echo/abc:/abc:" + message);
		eq(get("/echo/abc/d" + message), "GET:/echo/abc/d" + message + ":/abc/d" + message + ":");

		shutdown();
	}

}
