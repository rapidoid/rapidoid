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
import org.rapidoid.setup.Setup;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class HTTPServerStartupFailureTest extends IsolatedIntegrationTest {

	@Test
	public void shouldStartAndStop() {
		Setup x = Setup.create("x").port(5656);
		x.activate();
		x.shutdown();
	}

	@Test
	public void shouldFailIfCannotListen() {
		Setup.create("x").port(5656).activate();

		try {
			Setup.create("y").port(5656).activate();
		} catch (Exception e) {
			return; // OK
		}

		fail("Expected failure!");
	}

}
