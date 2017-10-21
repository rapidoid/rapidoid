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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.env.Env;
import org.rapidoid.test.AbstractCommonsTest;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class ConfigLoadingTest extends AbstractCommonsTest {

	@Test
	public void testPureConfig() {
		Config config = new ConfigImpl(); // no loading
		eq(config.toMap(), U.map());
	}

	@Test
	public void testCustomFileConfig() {
		Config config = new ConfigImpl("mycfg");

		eq(config.toMap(), U.map());

		config.setPath("my-cfg");

		eq(config.toMap(), U.map("z", "foo", "k", "hey"));

		Env.setProfiles("prof1", "prof2");
		config.invalidate();

		eq(config.toMap(), U.map("z", "bar", "k", "hey"));
	}

	@Test
	public void testJsonFileConfig() {
		Config config = new ConfigImpl("cfg");

		eq(config.toMap(), U.map());

		config.setPath("my-cfg");

		eq(config.toMap(), U.map("z", "foo2", "k", "hey2"));

		Env.setProfiles("prof1", "prof2");
		config.invalidate();

		eq(config.toMap(), U.map("z", "bar2", "k", "hey2"));
	}

}
