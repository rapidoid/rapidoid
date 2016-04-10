package org.rapidoid.config;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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
import org.rapidoid.commons.Env;
import org.rapidoid.test.AbstractCommonsTest;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class ConfigurationTest extends AbstractCommonsTest {

	@Test
	public void testBasicConfig() {
		isTrue(Env.dev());

		Conf.ROOT.set("abc", "123");
		Conf.ROOT.set("cool", true);
		Conf.ROOT.set("production", true);

		eq(Conf.ROOT.entry("abc").or(0).longValue(), 123);
		isTrue(Conf.ROOT.is("cool"));
		isTrue(Env.production());
		isFalse(Env.dev());

		checkDefaults();
	}

	@Test
	public void testDefaultConfig() {
		isTrue(Env.dev());

		checkDefaults();
	}

	private void checkDefaults() {
		eq(Conf.ON.entry("port").or(0).longValue(), 8888);
		eq(Conf.ON.entry("address").str().getOrNull(), "0.0.0.0");
	}

	@Test
	public void testProfiles() {
		Conf.args("port=12345", "profiles=mysql,prod");

		eq(Env.profiles(), U.set("mysql", "prod"));

		isFalse(Env.dev());
		isTrue(Env.production());

		checkDefaults();
	}

	@Test
	public void testDefaultProfiles() {
		eq(Env.profiles(), U.set("dev", "default"));
	}

}
