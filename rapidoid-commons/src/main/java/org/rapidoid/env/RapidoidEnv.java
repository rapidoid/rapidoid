package org.rapidoid.env;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.util.Msc;

import java.lang.reflect.Method;

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

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class RapidoidEnv extends RapidoidThing {

	private static volatile Method testMethod;

	public static synchronized void reset() {
		testMethod = null;
	}

	public static synchronized void touch() {
		detectCurrentTestMethod();
	}

	private static void detectCurrentTestMethod() {
		if (testMethod == null && (!Env.isInitialized() || Env.test())) {
			testMethod = Msc.getTestMethodIfExists();

			if (testMethod != null) {
				Log.info("Detected test method", "class", testMethod.getDeclaringClass().getSimpleName(), "name", testMethod.getName());
			}
		}
	}

	public static Method getTestMethod() {
		return testMethod;
	}

}
