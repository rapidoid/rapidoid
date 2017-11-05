package org.rapidoid.platform;

/*
 * #%L
 * rapidoid-platform
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
import org.rapidoid.deploy.AppDeployment;
import org.rapidoid.deploy.Apps;
import org.rapidoid.io.IO;
import org.rapidoid.io.Upload;
import org.rapidoid.u.U;
import org.rapidoid.util.MscOpts;

import java.util.Set;

import static org.rapidoid.test.TestCommons.createTempDir;

@Authors("Nikolche Mihajlovski")
@Since("5.4.7")
public class AppDeploymentTest extends PlatformTestCommons {

	@Test
	public void testAppDeployment() {
		String appsDir = createTempDir("apps");
		MscOpts.appsPath(appsDir);

		// 0 APPS

		isTrue(Apps.names().isEmpty());
		eq(ls(), U.set());

		// CREATE foo

		final Upload fooJar = new Upload("foo.jar", new byte[]{1, 2, 3});
		AppDeployment foo = AppDeployment.fromFilename(fooJar.filename());

		isFalse(foo.exists());
		isTrue(foo.isEmpty());

		// STAGE foo

		foo.stage(fooJar.filename(), fooJar.content());

		isTrue(foo.exists());
		isFalse(foo.isEmpty());

		eq(Apps.names(), U.set("foo"));
		eq(ls(), U.set("foo", "foo/foo.jar.staged"));

		// CREATE bar

		Upload barZip = new Upload("bar.zip", new byte[]{4, 5});
		AppDeployment bar = AppDeployment.fromFilename(barZip.filename());

		isFalse(bar.exists());
		isTrue(bar.isEmpty());

		// STAGE bar

		bar.stage(barZip.filename(), barZip.content());

		isTrue(bar.exists());
		isFalse(bar.isEmpty());

		eq(Apps.names(), U.set("foo", "bar"));
		eq(ls(), U.set("foo", "bar", "foo/foo.jar.staged", "bar/bar.zip.staged"));

		// DEPLOY foo

		foo.deploy();

		eq(ls(), U.set("foo", "bar", "foo/foo.jar", "bar/bar.zip.staged"));

		// DEPLOY bar

		bar.deploy();

		eq(ls(), U.set("foo", "bar", "foo/foo.jar", "bar/bar.zip"));

		// 2 APPS (foo and bar)

		eq(Apps.names(), U.set("foo", "bar"));

		// DELETE foo

		foo.delete();

		eq(Apps.names(), U.set("bar"));
		eq(ls(), U.set("bar", "bar/bar.zip"));

		// DELETE bar

		bar.delete();

		eq(Apps.names(), U.set());
		eq(ls(), U.set());
	}

	private Set<String> ls() {
		return U.set(IO.find().recursive().in(MscOpts.appsPath()).getRelativeNames());
	}

}
