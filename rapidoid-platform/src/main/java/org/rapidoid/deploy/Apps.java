package org.rapidoid.deploy;

/*-
 * #%L
 * rapidoid-platform
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
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.io.IO;
import org.rapidoid.platform.PlatformOpts;
import org.rapidoid.process.Processes;
import org.rapidoid.u.U;

import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.4.7")
public class Apps extends RapidoidThing {

	private static final Deployments DEPLOYMENTS = new Deployments();

	public static Processes processes() {
		return Processes.GROUP;
	}

	public static Deployments deployments() {
		return DEPLOYMENTS;
	}

	public static Set<String> names() {
		return PlatformOpts.isSingleApp()
			? U.set("app")
			: U.set(IO.find("*").folders().in(PlatformOpts.appsPath()).getRelativeNames());
	}

	public static void reload() {
		for (String name : names()) {
			if (!deployments().exists(name)) {
				AppDeployment app = AppDeployment.create(name);
				deployments().add(new ManageableApp(app));
			}
		}
	}
}
