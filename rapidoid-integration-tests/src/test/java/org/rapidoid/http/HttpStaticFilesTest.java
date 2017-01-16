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
import org.rapidoid.io.Res;
import org.rapidoid.setup.On;

@Authors("Nikolche Mihajlovski")
@Since("5.0.11")
public class HttpStaticFilesTest extends IsolatedIntegrationTest {

	@Test
	public void serveStaticFiles() {
		On.custom().staticFilesPath("static1", "non-existing-location", "static2");

		On.get("/c").managed(false).contentType(MediaType.JSON).serve("override");

		onlyGet("/"); // home page
		onlyGet("/index"); // home page
		onlyGet("/index.html"); // home page

		onlyGet("/a");
		onlyGet("/a.html");

		onlyGet("/b");
		onlyGet("/c");

		onlyGet("/dir1/sub1.txt");

		// no private files (starting with '.')
		notFound("/dir1/.sub2.txt");
		notFound("/.priv.txt");

		// no folders
		Res dir1 = Res.from("dir1", "static2");
		isFalse(dir1.exists());
		notFound("/dir1");

		notFound("/xx");
		notFound("/page1");
		notFound("/page2");
	}

	@Test
	public void serveStaticFilesFromDefaultLocations() {
		onlyGet("/page1");
		onlyGet("/page1.html");

		onlyGet("/page2");

		notFound("/");
		notFound("/xx");
	}

}
