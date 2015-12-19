package org.rapidoid.util;

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

import java.lang.reflect.Method;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class RuntimeInfo {

	private static final Method getGarbageCollectorMXBeans;

	static {
		Class<?> manFactory = Cls.getClassIfExists("java.lang.management.ManagementFactory");
		getGarbageCollectorMXBeans = manFactory != null ? Cls.getMethod(manFactory, "getGarbageCollectorMXBeans")
				: null;
	}

	public static String gcInfo() {
		String gcinfo = "";

		if (getGarbageCollectorMXBeans != null) {
			// FIXME resolve rapidoid-beany dependency
			// List<?> gcs = Cls.invokeStatic(getGarbageCollectorMXBeans);
			// for (Object gc : gcs) {
			// gcinfo += " | " + Beany.getPropValue(gc, "name") + " x" + Beany.getPropValue(gc, "collectionCount")
			// + ":" + Beany.getPropValue(gc, "collectionTime") + "ms";
			// }
		}
		return gcinfo;
	}

}
