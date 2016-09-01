package org.rapidoid.io.watch;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.config.RapidoidInitializer;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.util.Collection;
import java.util.Queue;

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

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class Watch extends RapidoidInitializer {

	private final WatcherThread watcher;

	public Watch(Collection<String> folders, FilesystemChangeListener changes) {
		this.watcher = new WatcherThread(changes, folders, true);
		watcher.start();
	}

	public static Watch dirs(Collection<String> folders, final FilesystemChangeListener changes) {
		try {
			return new Watch(folders, changes);

		} catch (Throwable e) {
			Log.error("Couldn't watch for changes!", e);
			return null;
		}
	}

	public static Watch dir(String folder, FilesystemChangeListener changes) {
		return dirs(U.list(folder), changes);
	}

	public static Watch dirs(Collection<String> folders, final ClassRefresher refresher) {
		try {
			Queue<String> created = Coll.queue();
			Queue<String> modified = Coll.queue();
			Queue<String> deleted = Coll.queue();

			FilesystemChangeQueueListener changes = new FilesystemChangeQueueListener(created, modified, deleted);
			new WatchingRefresherThread(folders, created, modified, deleted, refresher).start();

			return dirs(folders, changes);

		} catch (Throwable e) {
			Log.error("Couldn't watch for changes!", e);
			return null;
		}
	}

	public static Watch dir(String folder, ClassRefresher changes) {
		return dirs(U.list(folder), changes);
	}

	public void stop() {
		watcher.interrupt();
	}

}
