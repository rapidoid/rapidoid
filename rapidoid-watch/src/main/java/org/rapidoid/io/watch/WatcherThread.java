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

import org.rapidoid.activity.AbstractLoopThread;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.concurrent.CancellationException;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class WatcherThread extends AbstractLoopThread {

	private final Map<WatchKey, Path> keys = U.map();

	private final FilesystemChangeListener onChange;
	private final String path;
	private final File file;
	private final boolean recursive;

	private volatile WatchService watcher;

	public WatcherThread(FilesystemChangeListener change, String path, boolean recursive) {
		this.onChange = change;
		this.path = path;
		this.recursive = recursive;
		this.file = new File(path);

		setName("watcher");
	}

	private void startWatching() {
		Path dir = Paths.get(path);
		if (recursive) {
			startWatchingTree(dir);
		} else {
			startWatching(dir);
		}
	}

	private void startWatching(Path dir) {
		Log.info("Watching directory for changes", "dir", path, "recursive", recursive);

		try {
			if (watcher == null) {
				watcher = FileSystems.getDefault().newWatchService();
			}

			WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
			keys.put(key, dir);
		} catch (IOException e) {
			Log.error("Couldn't register to watch for changes on: " + dir, e);
		}
	}

	private void startWatchingTree(final Path root) {
		try {
			Dir.traverse(root, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					startWatching(dir);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (Exception e) {
			Log.warn("Couldn't register a watch for the directory tree", "dir", root);
		}
	}

	@Override
	protected void loop() {
		if (watcher == null) {
			if (file.exists()) {
				startWatching();
			} else {
				U.sleep(1000);
				return;
			}
		}

		WatchKey key;
		try {
			key = watcher.take();
		} catch (InterruptedException x) {
			throw new CancellationException();
		}

		Path dir = keys.get(key);
		if (dir == null) {
			return;
		}

		for (WatchEvent<?> event : key.pollEvents()) {
			WatchEvent.Kind<?> kind = event.kind();

			if (ENTRY_CREATE.equals(kind)) {

				Path child = getChild(dir, event);

				if (recursive && Files.isDirectory(child, NOFOLLOW_LINKS)) {
					startWatchingTree(child);
				}

				onChange.created(fullNameOf(child));

			} else if (ENTRY_MODIFY.equals(kind)) {
				Path child = getChild(dir, event);

				onChange.modified(fullNameOf(child));

			} else if (ENTRY_DELETE.equals(kind)) {
				Path child = getChild(dir, event);

				onChange.deleted(fullNameOf(child));

			} else if (OVERFLOW.equals(kind)) {
				Log.warn("Received OVERFLOW event from the Watch service!");

			} else {
				throw U.notExpected();
			}
		}

		boolean isKeyValid = key.reset();
		if (!isKeyValid) {
			keys.remove(key);

			if (keys.isEmpty()) {
				if (!file.exists()) {
					Log.warn("Cannot watch directory because it doesn't exist anymore", "dir", dir);
				} else {
					Log.error("Cannot watch directory due to unknown reason!", "dir", dir);
				}
				watcher = null;
			}
		}
	}

	private Path getChild(Path dir, WatchEvent<?> event) {
		WatchEvent<Path> ev = U.cast(event);
		Path path = ev.context();
		return dir.resolve(path);
	}

	private String fullNameOf(Path child) {
		return child.toAbsolutePath().toString();
	}

}
