package org.rapidoid.main;

/*
 * #%L
 * rapidoid-main
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
import java.util.List;

import org.rapidoid.annotation.App;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.appctx.Scan;
import org.rapidoid.cls.Cls;

@Authors("Nikolche Mihajlovski")
@Since("4.0.0")
public class Main {

	public static void main(String[] args) {
		MainHelp.processHelp(args);

		List<Class<?>> app = Scan.annotated(App.class);
		if (!app.isEmpty()) {
			Class<?> appCls = app.get(0);

			Method main = Cls.getMethod(appCls, "main", String[].class);
			if (main != null) {
				Object[] mainArgs = new Object[] { args };
				Cls.invokeStatic(main, mainArgs);
			}
		}
	}

}
