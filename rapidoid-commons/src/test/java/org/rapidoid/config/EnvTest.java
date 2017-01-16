package org.rapidoid.config;

/*
 * #%L
 * rapidoid-commons
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

import org.junit.Before;
import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.env.Env;
import org.rapidoid.env.EnvMode;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class EnvTest extends TestCommons {

	@Before
	public void reset() {
		Env.reset();
	}

	@Test
	public void testNoConfig() {
		eq(Env.args(), U.list());

		eq(Env.profiles(), U.set("default", "test")); // test is inferred in this test

		isTrue(Env.test());
		isFalse(Env.production());
		isFalse(Env.dev());
	}

	@Test
	public void testArgs() {
		Env.setArgs("mode=production", "foo=bar", "111");
		eq(Env.args(), U.list("mode=production", "foo=bar", "111"));
	}

	@Test
	public void testProductionMode() {
		Env.setArgs("mode=production", "foo=bar");

		eq(Env.profiles(), U.set("default", "production"));
		assertProductionMode();
	}

	@Test
	public void testDevMode() {
		Env.setArgs("mode=dev", "foo=bar");

		eq(Env.profiles(), U.set("default", "dev"));
		assertDevMode();
	}

	@Test
	public void testTestMode() {
		Env.setArgs("mode=test", "foo=bar");

		eq(Env.profiles(), U.set("default", "test"));
		assertTestMode();
	}

	@Test
	public void testProductionProfile() {
		Env.setArgs("profiles=production,mysql", "foo=bar");

		eq(Env.profiles(), U.set("mysql", "production"));
		assertProductionMode();
	}

	@Test
	public void testDevProfile() {
		Env.setArgs("profiles=abc,dev", "foo=bar");

		eq(Env.profiles(), U.set("abc", "dev"));
		assertDevMode();
	}

	@Test
	public void testTestProfile() {
		Env.setArgs("profiles=test", "foo=bar");

		eq(Env.profiles(), U.set("test"));
		assertTestMode();
	}

	@Test
	public void testCustomProfile() {
		Env.setArgs("profiles=abc", "foo=bar");

		eq(Env.profiles(), U.set("abc", "test")); // test is inferred in this test
		assertTestMode();
	}

	private void assertProductionMode() {
		eq(Env.mode(), EnvMode.PRODUCTION);
		isTrue(Env.production());
		isFalse(Env.test());
		isFalse(Env.dev());
	}

	private void assertDevMode() {
		eq(Env.mode(), EnvMode.DEV);
		isTrue(Env.dev());
		isFalse(Env.production());
		isFalse(Env.test());
	}

	private void assertTestMode() {
		eq(Env.mode(), EnvMode.TEST);
		isTrue(Env.test());
		isFalse(Env.production());
		isFalse(Env.dev());
	}

}
