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
import org.rapidoid.render.Templates;
import org.rapidoid.setup.My;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class HttpTemplatesPathTest extends IsolatedIntegrationTest {

	@Test
	public void testTemplatesPath1() {
		On.custom().templatesPath("test-templates");

		Templates.setPath("something-different");
		eq(Templates.getPath(), U.array("something-different"));

		setupAndTest();
	}

	@Test
	public void testTemplatesPath2() {
		On.custom().templatesPath("test-templates");
		eq(On.custom().templatesPath(), U.array("test-templates"));
		setupAndTest();
	}

	@Test
	public void testTemplatesPath3() {
		My.templatesPath("test-templates");
		eq(On.custom().templatesPath(), U.array("test-templates"));
		setupAndTest();
	}

	private void setupAndTest() {
		On.get("/t").mvc(req -> U.map("x", 123));
		onlyGet("/t");
	}

}
