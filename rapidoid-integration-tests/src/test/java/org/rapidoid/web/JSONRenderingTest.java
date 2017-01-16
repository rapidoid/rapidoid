package org.rapidoid.web;

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
import org.rapidoid.data.JSON;
import org.rapidoid.domain.Movie;
import org.rapidoid.http.IsolatedIntegrationTest;
import org.rapidoid.setup.On;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class JSONRenderingTest extends IsolatedIntegrationTest {

	@Test
	public void testJSONRendering() {
		On.get("/").json(() -> new Movie("Rambo", 1990));

		onlyGet("/");
	}

	@Test
	public void testJSONParsingWithoutJsonHeaderPOST() {
		// simply return the same object
		On.post("/movie").json((Movie m) -> m);

		Movie movie = new Movie("test title", 1999);
		onlyPost("/movie", JSON.stringify(movie));
	}

	@Test
	public void testJSONParsingWithoutJsonHeaderPUT() {
		// simply return the same object
		On.put("/movie").json((Movie m) -> m);

		Movie movie = new Movie("test title", 1999);
		onlyPut("/movie", JSON.stringify(movie));
	}

}
