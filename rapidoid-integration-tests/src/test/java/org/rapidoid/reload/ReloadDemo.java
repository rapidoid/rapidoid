package org.rapidoid.reload;

/*
 * #%L
 * rapidoid-integration-tests
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

import org.rapidoid.io.watch.ClassRefresher;
import org.rapidoid.io.watch.Watch;
import org.rapidoid.log.Log;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.util.D;
import org.rapidoid.wire.Wire;

import java.util.List;
import java.util.Set;

/**
 * Demo for class reloading. E.g. try changing the Abc class...
 */
public class ReloadDemo {

	public static void main(String[] args) {

		Set<String> cps = ClasspathUtil.getClasspathFolders();

		Watch watch = Watch.dirs(cps, new ClassRefresher() {
			@Override
			public void refresh(List<Class<?>> classes) {
				Log.info("Refreshed classes", "classes", classes);
				for (Class<?> cls : classes) {
					refreshf(cls);
				}
			}
		});
	}

	private static void refreshf(Class<?> cls) {
		Object ins = Wire.singleton(cls);
		D.print(ins);
	}

}
