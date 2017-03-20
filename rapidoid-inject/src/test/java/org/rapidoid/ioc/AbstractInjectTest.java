package org.rapidoid.ioc;

/*
 * #%L
 * rapidoid-inject
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
import org.rapidoid.config.Conf;
import org.rapidoid.data.JSON;
import org.rapidoid.test.TestCommons;
import org.rapidoid.util.Msc;

public abstract class AbstractInjectTest extends TestCommons {

	@Before
	public void openContext() {
		Msc.reset();
		Conf.ROOT.setPath(getTestNamespace());
		IoC.reset();
	}

	protected void verify(String name, Object actual) {
		String json = JSON.prettify(actual).replaceAll("@\\w+", "@");
		super.verifyCase(name, json, name);
	}

	protected void verifyIoC() {
		verify("ioc", IoC.defaultContext().info());
	}

	protected void verifyIoC(String name) {
		verify(name, IoC.defaultContext().info());
	}

	protected void noIocBean(Class<?> clazz) {
		try {
			IoC.singleton(clazz);
			fail("Expected IoC exception for class: " + clazz);
		} catch (RuntimeException e) {
			// ok
		}
	}

}
