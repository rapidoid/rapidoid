package org.rapidoid.net;

/*
 * #%L
 * rapidoid-net
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import org.rapidoid.util.U;

public class StatsThread extends Thread {

	public StatsThread() {
		super("stats");
	}

	private boolean active;

	private String lastStats;

	public synchronized void execute() {
		if (!active) {
			active = true;
			start();
		}
	}

	@Override
	public void run() {
		U.info("Starting stats thread...");
		while (true) {
			U.sleep(1000);
			String stats = U.getCpuMemStats();
			if (!stats.equals(lastStats)) {
				U.print(stats);
				lastStats = stats;
			}
		}
	}

}
