/*-
 * #%L
 * rapidoid-watch
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
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

package org.rapidoid.io.watch;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.commons.RapidoidInitializer;
import org.rapidoid.lambda.Operation;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.util.Collection;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class Watch extends RapidoidInitializer {

	private static final Set<WatcherThread> WATCHERS = Coll.synchronizedSet();

	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				cancelAll();
			}
		});
	}

	public static WatcherThread dirs(Collection<String> folders, final FilesystemChangeListener changes) {
		try {

			WatcherThread watcher = new WatcherThread(changes, folders, true);
			watcher.start();
			WATCHERS.add(watcher);

			return watcher;

		} catch (Throwable e) {
			Log.error("Couldn't watch for changes!", e);
			return null;
		}
	}

	public static WatcherThread dir(String folder, FilesystemChangeListener changes) {
		return dirs(U.list(folder), changes);
	}

	public static WatcherThread dirs(Collection<String> folders, final ClassRefresher refresher, Predicate<String> veto) {
		try {
			Queue<String> created = Coll.queue();
			Queue<String> modified = Coll.queue();
			Queue<String> deleted = Coll.queue();

			FilesystemChangeQueueListener changes = new FilesystemChangeQueueListener(created, modified, deleted);
			new WatchingRefresherThread(folders, created, modified, deleted, refresher, veto).start();

			return dirs(folders, changes);

		} catch (Throwable e) {
			Log.error("Couldn't watch for changes!", e);
			return null;
		}
	}

	public static WatcherThread dir(String folder, ClassRefresher changes, Predicate<String> veto) {
		return dirs(U.list(folder), changes, veto);
	}

	public static WatcherThread dirs(Collection<String> folders, final Operation<String> changeListener) {
		return dirs(folders, simpleListener(changeListener));
	}

	public static WatcherThread dir(String folder, final Operation<String> changeListener) {
		return dir(folder, simpleListener(changeListener));
	}

	public static FilesystemChangeListener simpleListener(final Operation<String> changeListener) {
		return new FilesystemChangeListener() {
			@Override
			public void created(String filename) throws Exception {
				changeListener.execute(filename);
			}

			@Override
			public void modified(String filename) throws Exception {
				changeListener.execute(filename);
			}

			@Override
			public void deleted(String filename) throws Exception {
				changeListener.execute(filename);
			}
		};
	}

	public static void cancelAll() {
		synchronized (WATCHERS) {
			for (WatcherThread watch : WATCHERS) {
				watch.cancel();
			}

			WATCHERS.clear();
		}
	}

}
