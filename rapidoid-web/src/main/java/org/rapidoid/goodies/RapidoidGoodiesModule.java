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
import org.rapidoid.log.Log;
import org.rapidoid.setup.Admin;
import org.rapidoid.setup.Dev;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class RapidoidGoodiesModule {

	static {
		if (Dev.setup().withGoodies()) {
			Log.info("Activating Dev goodies");

			Dev.page("/").render(new RoutesHandler());
			Dev.page("/config").render(new ConfigHandler());
		}

		if (Admin.setup().withGoodies()) {
			Log.info("Activating Admin goodies");

			Admin.page("/").render(Goodies.graphs());
			Admin.page("/routes").render(new RoutesHandler());

			Admin.page("/jmx/memory").render(Goodies.memory());
			Admin.page("/jmx/mempool").render(Goodies.memoryPool());
			Admin.page("/jmx/classes").render(Goodies.classes());
			Admin.page("/jmx/os").render(Goodies.os());
			Admin.page("/jmx/threads").render(Goodies.threads());
			Admin.page("/jmx/compilation").render(Goodies.compilation());
			Admin.page("/jmx/runtime").render(Goodies.runtime());
			Admin.page("/jmx/gc").render(Goodies.gc());
		}
	}

}