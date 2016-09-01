package org.rapidoid.config;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.RapidoidInfo;
import org.rapidoid.log.Log;
import org.rapidoid.log.LogbackUtil;
import org.rapidoid.util.Msc;

import java.lang.management.ManagementFactory;

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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class RapidoidInitializer extends RapidoidThing {

	static {
		initialize();
	}

	private static volatile boolean initialized;

	public static synchronized void initialize() {
		if (!initialized) {
			initialized = true;

			String ver = RapidoidInfo.version();
			String proc = ManagementFactory.getRuntimeMXBean().getName();
			String dir = System.getProperty("user.dir");

			Log.info("!Starting Rapidoid...", "version", ver, "process", proc, "java", Msc.javaVersion(), "dir", dir);

			if (Msc.hasLogback()) {
				LogbackUtil.setupLogger();
			}

			Cls.getClassIfExists("org.rapidoid.insight.Metrics");
		}
	}

}
