package org.rapidoid.ctx;

/*
 * #%L
 * rapidoid-ctx
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("4.4.0")
public class RolesTest extends TestCommons {

	@Test
	public void testRolesConfig() {
		eq(Roles.getRolesFor("niko"), U.set("owner", "moderator", "admin"));
		eq(Roles.getRolesFor("chuck"), U.set("moderator", "restarter"));
		eq(Roles.getRolesFor("abc"), U.set("guest"));

		eq(Roles.getRolesFor("zzz"), U.set("logged_in"));

		eq(Roles.getRolesFor(""), U.set("anonymous"));
		eq(Roles.getRolesFor(null), U.set("anonymous"));
	}

}
