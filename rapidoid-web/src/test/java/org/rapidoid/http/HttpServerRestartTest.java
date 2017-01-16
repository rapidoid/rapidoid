package org.rapidoid.http;

/*
 * #%L
 * rapidoid-web
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
import org.rapidoid.setup.On;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class HttpServerRestartTest extends HttpTestCommons {

	@Test
	public void shouldHandleRestarts() {
		On.get("/").html("a");
		eq(get("/"), "a");

		On.setup().http().resetConfig();

		On.get("/").html("b");
		eq(get("/"), "b");
	}

	@Test
	public void shouldHandleRestartsX() {
		On.get("/").html("x");
		eq(get("/"), "x");
	}

	@Test
	public void shouldHandleRestartsY() {
		On.get("/").html("y");
		eq(get("/"), "y");
	}

}
