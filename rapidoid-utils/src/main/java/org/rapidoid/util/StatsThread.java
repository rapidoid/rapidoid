package org.rapidoid.util;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;

/*
 * #%L
 * rapidoid-utils
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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class StatsThread extends Thread {

	public StatsThread() {
		super("stats");
	}

	private String lastStats;

	@Override
	public void run() {
		Log.info("Starting stats thread...");

		while (!Thread.interrupted()) {
			U.sleep(1000);
			String stats = UTILS.getCpuMemStats();
			if (!stats.equals(lastStats)) {
				System.out.println(stats);
				lastStats = stats;
			}
		}

		Log.info("Stopped stats thread.");
	}

}
