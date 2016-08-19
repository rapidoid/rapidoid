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
import org.rapidoid.io.IO;
import org.rapidoid.log.Log;
import org.rapidoid.test.AbstractCommonsTest;
import org.rapidoid.u.U;

import java.io.File;

@Authors("Nikolche Mihajlovski")
@Since("4.4.0")
public class RefreshingConfigTest extends AbstractCommonsTest {

	@Test
	public void testRefreshingConfig() {
		File tmp = createTempFile();
		Log.info("Created temporary file", "file", tmp);

		Config config = new ConfigImpl();
		ConfigUtil.autoRefresh(config, tmp.getAbsolutePath());

		eq(config.toMap(), U.map());

		U.sleep(2000); // wait 2 seconds, last modified has 1 second resolution

		IO.save(tmp.getAbsolutePath(), "a: 1\nbb: cd\n");

		U.sleep(2000); // wait for the config to auto-refresh

		eq(config.toMap(), U.map("a", 1, "bb", "cd"));

		IO.save(tmp.getAbsolutePath(), "a: xyz\nccc: 2\n");
		U.sleep(2000); // wait for the config to auto-refresh

		eq(config.toMap(), U.map("a", "xyz", "bb", "cd", "ccc", 2));
	}

}
