package org.rapidoid.test;

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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.rapidoid.RapidoidModule;
import org.rapidoid.RapidoidModules;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.IntegrationTest;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Metadata;
import org.rapidoid.env.Env;
import org.rapidoid.log.Log;
import org.rapidoid.util.Msc;

@Authors("Nikolche Mihajlovski")
@Since("5.1.6")
public abstract class RapidoidIntegrationTest extends RapidoidTest {

	@Before
	public final void beforeRapidoidTest() {

		Log.info("--------------------------------------------------------------------------------");
		Log.info("@" + Msc.processId() + " TEST " + getClass().getCanonicalName());
		Log.info("--------------------------------------------------------------------------------");

		clearErrors();

		isTrue(Msc.isInsideTest());
		isTrue(Env.test());

		before(this);
		start(this);
	}

	@After
	public final void afterRapidoidTest() {
		after(this);

		if (hasError()) {
			Assert.fail("Assertion error(s) occurred, probably were caught or were thrown on non-main thread!");
		}
	}

	public static void before(Object test) {
		Msc.logSection("INITIALIZE");

		for (RapidoidModule mod : RapidoidModules.getAllAvailable()) {
			Log.debug("Initializing module before the test", "module", mod.name(), "order", mod.order());
			mod.beforeTest(test);
		}

		Log.debug("All modules are initialized");
	}

	public static void start(Object test) {
		runMainClass(test);

		for (RapidoidModule mod : RapidoidModules.getAllAvailable()) {
			Log.debug("Initializing the test", "module", mod.name(), "order", mod.order());
			mod.initTest(test);
		}

		Msc.logSection("START TEST");
	}

	private static void runMainClass(Object test) {
		IntegrationTest testInfo = Metadata.getAnnotationRecursive(test.getClass(), IntegrationTest.class);

		if (testInfo != null) {
			Msc.logSection("RUN MAIN");

			Class<?> main = testInfo.main();
			String[] args = testInfo.args();

			Msc.invokeMain(main, args);
		}
	}

	public static void after(Object test) {
		Msc.logSection("END TEST");

		for (RapidoidModule mod : RapidoidModules.getAllAvailable()) {
			mod.afterTest(test);
		}

		Msc.logSection("FINISHED");
	}

}
