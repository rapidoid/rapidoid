package org.rapidoid.goodies;

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
import org.rapidoid.env.Env;
import org.rapidoid.commons.RapidoidInfo;
import org.rapidoid.config.Conf;
import org.rapidoid.http.IsolatedIntegrationTest;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.setup.App;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.2.3")
public class StatusHandlerTest extends IsolatedIntegrationTest {

	@Test
	public void testDefault() throws Exception {
		Env.reset();

		Conf.ROOT.set("id", "foo-bar");

		verify("in-test", status().toString());
	}

	@Test
	public void testAppSetup() throws Exception {
		Env.reset();

		App.run(new String[0], "profiles=mysql", "mode=production");
		Conf.ROOT.set("id", "rpd123");
		ClasspathUtil.appJar("/a/b.jar");

		verify("app-setup", status().toString());
	}

	private Map<String, ?> status() throws Exception {
		Map<String, ?> status = new StatusHandler().call();

		eq(status.remove("version"), RapidoidInfo.version());

		String uptime = (String) status.remove("uptime");
		notNull(uptime);
		isTrue(uptime.matches("\\d+s"));

		return status;
	}

}
