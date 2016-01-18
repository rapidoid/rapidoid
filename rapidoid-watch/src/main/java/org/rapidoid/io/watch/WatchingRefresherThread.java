package org.rapidoid.io.watch;

/*
 * #%L
 * rapidoid-watch
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

import java.io.File;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.rapidoid.activity.AbstractLoopThread;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class WatchingRefresherThread extends AbstractLoopThread {

	private final ClassRefresher refresher;

	private final Set<String> filenames = U.set();

	private final String dir;

	private final Queue<String> queue;

	public WatchingRefresherThread(String dir, Queue<String> queue, ClassRefresher refresher) {
		this.dir = dir;
		this.queue = queue;
		this.refresher = refresher;
	}

	@Override
	protected void loop() {
		boolean found = false;
		String filename;

		while ((filename = queue.poll()) != null) {
			filenames.add(filename);
			found = true;
		}

		if (!found && !filenames.isEmpty()) {
			reload(filenames);
			filenames.clear();
		}

		U.sleep(100);
	}

	protected void reload(Set<String> filenames) {
		Log.info("Reloading classes", "classes", filenames);

		try {
			List<String> classnames = filenamesToClassnames(dir, filenames);
			List<Class<?>> classes = Reload.reloadClasses(dir, classnames);

			refresher.refresh(classes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String filenameToClassname(String dir, String filename) {
		if (filename.endsWith(".class") && filename.startsWith(dir + File.separatorChar)) {
			return filename.substring(dir.length() + 1, filename.length() - 6).replace(File.separatorChar, '.');
		}

		if (filename.endsWith(".class") && filename.startsWith(dir + "/")) {
			return filename.substring(dir.length() + 1, filename.length() - 6).replace('/', '.');
		}

		return null;
	}

	private static List<String> filenamesToClassnames(String dir, Set<String> filenames) {
		List<String> list = U.list();

		for (String filename : filenames) {
			String classname = filenameToClassname(dir, filename);
			if (classname != null) {
				list.add(classname);
			}
		}

		return list;
	}

}
