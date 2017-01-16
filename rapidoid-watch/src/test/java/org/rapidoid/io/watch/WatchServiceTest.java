package org.rapidoid.io.watch;

/*
 * #%L
 * rapidoid-watch
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
import org.rapidoid.io.IO;
import org.rapidoid.io.Res;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.io.File;
import java.io.IOException;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class WatchServiceTest extends TestCommons {

	@Test
	public void testDirRefresh() throws IOException {
		String tmpPath = createTempDir("watch-service-test");

		Dir dir = Dir.from(tmpPath);
		Dir dir2 = Dir.from(tmpPath);

		isTrue(dir == dir2);

		giveItTimeToRefresh();

		// CREATE FILE a.tmp

		String fileA = Msc.path(tmpPath, "a.tmp");
		IO.save(fileA, "aa");

		giveItTimeToRefresh();

		Res resA = Res.from(new File(fileA));
		eq(dir.files(), U.set(resA));
		eq(dir.folders(), U.set());

		// CREATE FILE b.tmp

		String fileB = Msc.path(tmpPath, "b.tmp");
		IO.save(fileB, "bb");

		giveItTimeToRefresh();

		Res resB = Res.from(new File(fileB));
		eq(dir.files(), U.set(resA, resB));
		eq(dir.folders(), U.set());

		// CREATE FOLDER ccc

		String dirC = Msc.path(tmpPath, "ccc");
		isTrue(new File(dirC).mkdir());

		giveItTimeToRefresh();

		eq(dir.files(), U.set(resA, resB));
		eq(dir.folders(), U.set(dirC));

		// DELETE FILE a.tmp

		isTrue(new File(fileA).delete());

		giveItTimeToRefresh();

		eq(dir.files(), U.set(resB));
		eq(dir.folders(), U.set(dirC));

		// MODIFY FILE b.tmp

		IO.save(fileB, "bbbbb");

		giveItTimeToRefresh();

		eq(dir.files(), U.set(resB));
		eq(dir.folders(), U.set(dirC));

		// CREATE FILE ccc/x.tmp

		String fileX = Msc.path(tmpPath, "ccc", "x.tmp");
		IO.save(fileX, "x");

		giveItTimeToRefresh();

		Res resX = Res.from(new File(fileX));
		eq(dir.files(), U.set(resB, resX));
		eq(dir.folders(), U.set(dirC));

		// DELETE FILE ccc/x.tmp

		isTrue(new File(fileX).delete());

		giveItTimeToRefresh();

		eq(dir.files(), U.set(resB));
		eq(dir.folders(), U.set(dirC));

		// DELETE FOLDER ccc

		isTrue(new File(dirC).delete());

		giveItTimeToRefresh();

		eq(dir.files(), U.set(resB));
		eq(dir.folders(), U.set());

		// DELETE FILE b.tmp

		isTrue(new File(fileB).delete());

		giveItTimeToRefresh();

		eq(dir.files(), U.set());
		eq(dir.folders(), U.set());
	}

	private void giveItTimeToRefresh() {
		U.sleep(1000);
	}

}
