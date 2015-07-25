package org.rapidoid.io.watch;

/*
 * #%L
 * rapidoid-watch
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.test.TestCommons;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class WatchServiceTest extends TestCommons {

	public void justDemo() {
		Watch watch = Watch.dir("/tmp/classes", new ClassRefresher() {
			@Override
			public void refresh(List<Class<?>> classes) {
				Log.info("Refreshed classes", "classes", classes);
			}
		});

		U.sleep(1000);

		watch.stop();
	}

	@Test
	public void testDirRefresh() throws IOException {
		Path tmpDir = Files.createTempDirectory("watch-service-test");
		String tmpPath = tmpDir.toAbsolutePath().toString();
		tmpDir.toFile().deleteOnExit();

		Dir dir = Dir.from(tmpPath);
		Dir dir2 = Dir.from(tmpPath);

		isTrue(dir == dir2);

		// FIXME change the directory content and check the changes

		U.sleep(1000);
	}

}
