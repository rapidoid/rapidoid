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
import org.rapidoid.io.IO;
import org.rapidoid.test.AbstractCommonsTest;
import org.rapidoid.util.Msc;

@Authors("Nikolche Mihajlovski")
@Since("5.2.5")
public class RootCustomizationTest extends AbstractCommonsTest {

	@Before
	public void reset() {
		Env.reset();
		Conf.reset();
	}

	@Test
	public void testConfigRootSetup() {
		String dir = createTempDir("app");

		IO.save(Msc.path(dir, "config.yml"), "id: abc1");

		Env.setArgs("root=" + dir, "config=config");

		eq(Conf.ROOT.entry("id").getOrNull(), "abc1");
	}

	@Test
	public void testConfigRootAndFileSetup() {
		String dir = createTempDir("app");

		IO.save(Msc.path(dir, "the-config.yml"), "id: abc2");

		Env.setArgs("root=" + dir, "config=the-config");

		eq(Conf.ROOT.entry("id").getOrNull(), "abc2");
	}

}
