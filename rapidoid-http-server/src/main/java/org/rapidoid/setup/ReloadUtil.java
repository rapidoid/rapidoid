/*-
 * #%L
 * rapidoid-http-server
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
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

package org.rapidoid.setup;

import org.rapidoid.RapidoidModules;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.reload.Reload;

import java.util.function.Predicate;

@Authors("Nikolche Mihajlovski")
@Since("5.1.4")
public class ReloadUtil extends RapidoidThing {

	public static ClassLoader reloader() {
		Predicate<String> veto = ReloadUtil::isReloadForbidden;
		return Reload.createClassLoader(veto);
	}

	private static boolean isReloadForbidden(String classname) {
		return RapidoidModules.getAllAvailable().stream()
			.anyMatch(module -> module.preventsClassReload(classname));
	}

}
