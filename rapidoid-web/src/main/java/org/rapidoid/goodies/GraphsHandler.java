package org.rapidoid.goodies;

/*
 * #%L
 * rapidoid-web
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;
import org.rapidoid.insight.Metrics;

import java.util.concurrent.Callable;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class GraphsHandler extends GUI implements Callable<Object> {

	@Override
	public Object call() throws Exception {
		Object sysCpu = dygraph("System CPU", Metrics.SYSTEM_CPU);
		Object userCpu = dygraph("Process CPU", Metrics.PROCESS_CPU);

		Object memTotal = dygraph("Total JVM memory (MB)", Metrics.MEM_TOTAL);
		Object memUsed = dygraph("Used JVM memory (MB)", Metrics.MEM_USED);

		return multi(row(col6(sysCpu), col6(userCpu)), row(col6(memTotal), col6(memUsed)));
	}

}
