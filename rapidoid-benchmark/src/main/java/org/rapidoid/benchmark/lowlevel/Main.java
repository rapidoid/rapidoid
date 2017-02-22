package org.rapidoid.benchmark.lowlevel;

/*
 * #%L
 * rapidoid-benchmark
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.config.Conf;
import org.rapidoid.setup.App;

public class Main extends RapidoidThing {

	public static void main(String[] args) {
		App.run(args);

		Conf.HTTP.set("maxPipeline", 128);
		Conf.HTTP.set("timeout", 0);

		new PlaintextAndJsonServer().listen(8080);
	}

}
