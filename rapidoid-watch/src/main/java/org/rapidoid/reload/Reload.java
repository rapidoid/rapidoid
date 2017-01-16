package org.rapidoid.reload;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.u.U;

import java.util.Collection;
import java.util.List;

/*
 * #%L
 * rapidoid-watch
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
@Since("4.1.0")
public class Reload extends RapidoidThing {

	public static ClassReloader createClassLoader() {
		return createClassLoader(ClasspathUtil.getClasspathFolders());
	}

	public static ClassReloader createClassLoader(Collection<String> classpath) {
		Log.debug("Creating class loader", "classpath", classpath);
		ClassLoader parentClassLoader = ClassReloader.class.getClassLoader();
		return new ClassReloader(classpath, parentClassLoader, U.<String>list());
	}

	public static synchronized List<Class<?>> reloadClasses(Collection<String> classpath, List<String> classnames) {
		ClassReloader classLoader = Reload.createClassLoader(classpath);

		List<Class<?>> classes = U.list();

		for (String className : classnames) {
			try {
				classes.add(classLoader.loadClass(className));
			} catch (Throwable e) {
				Log.debug("Couldn't reload class!", "error", e);
			}
		}

		return classes;
	}

}
