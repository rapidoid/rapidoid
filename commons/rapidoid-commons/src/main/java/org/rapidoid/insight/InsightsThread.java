package org.rapidoid.insight;

import org.rapidoid.activity.RapidoidThread;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

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

/**
 * @author Nikolche Mihajlovski
 * @since 4.1.0
 */
public class InsightsThread extends RapidoidThread {

	public InsightsThread() {
		super("insights");
		setDaemon(true);
	}

	@Override
	public void run() {
		Log.info("Starting Insights thread...");

		while (!Thread.interrupted()) {
			U.sleep(1000);
			String stats = Insights.getCpuMemStats() + " :: " + Insights.getInfo();
			Log.info(stats);
		}

		Log.info("Stopped Insights thread.");
	}

}
