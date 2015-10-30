package org.rapidoid.io.watch;

/*
 * #%L
 * rapidoid-watch
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

import java.util.Queue;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class Watch {

	private final WatcherThread watcher;

	public Watch(String dir, FilesystemChangeListener changes) {
		this.watcher = new WatcherThread(changes, dir, true);
		watcher.start();
	}

	public static Watch dir(final String dir, final FilesystemChangeListener changes) {
		try {
			return new Watch(dir, changes);

		} catch (Throwable e) {
			Log.error("Couldn't watch for changes!", e);
			return null;
		}
	}

	public static Watch dir(final String dir, final ClassRefresher refresher) {
		try {
			Queue<String> queue = U.queue();
			FilesystemChangeQueueListener changes = new FilesystemChangeQueueListener(queue);
			new WatchingRefresherThread(dir, queue, refresher).start();
			return dir(dir, changes);

		} catch (Throwable e) {
			Log.error("Couldn't watch for changes!", e);
			return null;
		}
	}

	public void stop() {
		watcher.interrupt();
	}

}
