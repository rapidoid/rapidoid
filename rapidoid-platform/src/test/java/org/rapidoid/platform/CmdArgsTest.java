package org.rapidoid.platform;

/*-
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
import org.rapidoid.u.U;

import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.4.5")
public class CmdArgsTest extends PlatformTestCommons {

	@Test
	public void testEmptyArgs() {
		CmdArgs args = CmdArgs.from(U.list());

		eq(args.all, U.list());
		isNull(args.command);
		eq(args.args, U.list());
		eq(args.options, U.list());
		eq(args.special, U.list());
	}

	@Test
	public void testNoDevArgs() {
		CmdArgs args = CmdArgs.from(U.list("aaa", "bbb=true", "c=123", "/x->foo"));

		eq(args.all, U.list("aaa", "bbb=true", "c=123", "/x->foo"));
		eq(args.command, "aaa");
		eq(args.args, U.list("bbb=true", "c=123", "/x->foo"));
		eq(args.options, U.list("bbb=true", "c=123"));
		eq(args.special, U.list("/x->foo"));
	}

	@Test
	public void testFindAndReplaceDev() {
		CmdArgs args = CmdArgs.from(U.list("dev"));

		eq(args.all, U.list(CmdArgs.DEV_CMD_ARGS));
		eq(args.command, CmdArgs.CMD_PLATFORM);
		eq(args.args, U.list("mode=dev", "app.services=center", "users.admin.password=admin", "secret=NONE", "/ -> localhost:8080"));
		eq(args.options, U.list("mode=dev", "app.services=center", "users.admin.password=admin", "secret=NONE"));
		eq(args.special, U.list("/ -> localhost:8080"));
	}

	@Test
	public void testFindAndReplaceDevWithArgs() {
		CmdArgs args = CmdArgs.from(U.list("dev", "foo=123"));

		List<String> expected = U.list(CmdArgs.DEV_CMD_ARGS);
		expected.add("foo=123");

		eq(args.all, expected);
		eq(args.command, CmdArgs.CMD_PLATFORM);

		eq(args.options, U.list("mode=dev", "app.services=center", "users.admin.password=admin", "secret=NONE", "foo=123"));
		eq(args.special, U.list("/ -> localhost:8080"));
	}

}
