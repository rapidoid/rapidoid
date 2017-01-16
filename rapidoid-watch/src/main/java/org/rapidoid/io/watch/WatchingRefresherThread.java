package org.rapidoid.io.watch;

import org.rapidoid.activity.AbstractLoopThread;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.reload.Reload;
import org.rapidoid.u.U;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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
public class WatchingRefresherThread extends AbstractLoopThread {

	private static final AtomicInteger idGen = new AtomicInteger();

	private final ClassRefresher refresher;

	private final Set<String> createdFilenames = U.set();
	private final Set<String> modifiedFilenames = U.set();
	private final Set<String> deletedFilenames = U.set();

	private final Collection<String> folders;

	private final Queue<String> created;
	private final Queue<String> modified;
	private final Queue<String> deleted;

	public WatchingRefresherThread(Collection<String> folders, Queue<String> created, Queue<String> modified, Queue<String> deleted, ClassRefresher refresher) {
		this.folders = folders;
		this.created = created;
		this.modified = modified;
		this.deleted = deleted;
		this.refresher = refresher;

		setName("reloader" + idGen.incrementAndGet());
	}

	@Override
	protected void loop() {
		boolean found = false;
		String filename;

		while ((filename = created.poll()) != null) {
			createdFilenames.add(filename);
			found = true;
		}
		while ((filename = modified.poll()) != null) {
			modifiedFilenames.add(filename);
			found = true;
		}
		while ((filename = deleted.poll()) != null) {
			deletedFilenames.add(filename);
			found = true;
		}

		// when the changes are complete
		if (!found && (!createdFilenames.isEmpty() || !modifiedFilenames.isEmpty() || !deletedFilenames.isEmpty())) {
			reload();
			createdFilenames.clear();
			modifiedFilenames.clear();
			deletedFilenames.clear();
		}

		U.sleep(100);
	}

	protected void reload() {
		Set<String> filenames = U.set();
		filenames.addAll(createdFilenames);
		filenames.addAll(modifiedFilenames);

		Log.debug("Detected changes in classes", "created", createdFilenames, "modified", modifiedFilenames, "deleted", deletedFilenames);

		try {
			List<String> classnames = filenamesToClassnames(filenames);
			List<Class<?>> classes = Reload.reloadClasses(folders, classnames);
			refresher.refresh(classes, filenamesToClassnames(deletedFilenames));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String filenameToClassname(String filename) {
		for (String dir : folders) {
			if (filename.endsWith(".class") && filename.startsWith(dir + File.separatorChar)) {
				return filename.substring(dir.length() + 1, filename.length() - 6).replace(File.separatorChar, '.');
			}

			if (filename.endsWith(".class") && filename.startsWith(dir + "/")) {
				return filename.substring(dir.length() + 1, filename.length() - 6).replace('/', '.');
			}
		}

		return null;
	}

	private List<String> filenamesToClassnames(Set<String> filenames) {
		List<String> list = U.list();

		for (String filename : filenames) {
			String classname = filenameToClassname(filename);

			if (classname != null) {
				list.add(classname);
			}
		}

		return list;
	}

}
