package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.List;
import java.util.concurrent.Callable;

/*
 * #%L
 * rapidoid-web
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
@Since("5.1.0")
public class ClasspathHandler extends GUI implements Callable<Object> {

	@Override
	public Object call() throws Exception {
		List<Object> info = U.list();

		if (!Msc.isPlatform()) {
			info.add(h3("Application JAR:"));

			if (ClasspathUtil.hasAppJar()) {
				info.add(h4(ClasspathUtil.appJar()));
			} else {
				info.add(h4(WARN, " No ", b("app.jar"), " file was configured on the classpath!"));
			}
		}

		info.add(h3("Classpath folders:"));

		info.add(grid(ClasspathUtil.getClasspathFolders()).columns("trim").headers("Classpath entries (folders)").pageSize(0));

		info.add(h3("Classpath JARs:"));

		info.add(grid(ClasspathUtil.getClasspathJars()).columns("trim").headers("Classpath entries (JARs)").pageSize(0));

		return multi(info);
	}

}
