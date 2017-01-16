package org.rapidoid.integration.guice;

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
import org.rapidoid.http.IsolatedIntegrationTest;
import org.rapidoid.http.Self;
import org.rapidoid.integrate.Integrate;
import org.rapidoid.ioc.Beans;
import org.rapidoid.setup.App;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class GuiceIntegrationTest extends IsolatedIntegrationTest {

	@Test
	public void testGuiceIntegration() {
		Beans beans = Integrate.guice(new MathModule());
		App.register(beans);

		Self.get("/add?x=6&y=4").expect().entry("sum", 10);
		Self.get("/add?x=1&y=22").expect().entry("sum", 23);
	}

}

