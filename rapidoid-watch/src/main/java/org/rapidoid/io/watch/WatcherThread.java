package org.rapidoid.io.watch;

import org.rapidoid.activity.AbstractLoopThread;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.commons.Err;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

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
public class WatcherThread extends AbstractLoopThread {

	private static final AtomicInteger idGen = new AtomicInteger();

	private final WatchService watchService;

	private final Map<WatchKey, Path> keys = U.map();

	private final FilesystemChangeListener onChange;

	private final boolean recursive;

	private final List<String> folders = Coll.synchronizedList();

	private final Set<String> watching = Coll.synchronizedSet();

	WatcherThread(FilesystemChangeListener change, Collection<String> targetFolders, boolean recursive) {
		this.onChange = change;
		this.recursive = recursive;

		for (String folder : targetFolders) {
			this.folders.add(new File(folder).getAbsolutePath());
		}

		setName("watcher" + idGen.incrementAndGet());

		try {
			watchService = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			throw U.rte("Couldn't create a file system watch service!", e);
		}
	}

	private void init() {
		for (String folder : folders) {
			init(folder);
		}
	}

	private void init(String folder) {
		if (!watching.contains(folder) && new File(folder).exists()) {
			Log.debug("Watching folder for changes", "folder", folder, "recursive", recursive);

			Path dir = Paths.get(folder);
			if (recursive) {
				startWatchingTree(dir);
			} else {
				init(dir);
			}

			watching.add(folder);
		}
	}

	private void init(Path dir) {
		Log.debug("Registering directory watch", "dir", dir);

		try {
			WatchKey key = dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
			U.notNull(key, "watch key");

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
					init(dir);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (Exception e) {
			Log.warn("Couldn't register a watch for the directory tree", "dir", root);
		}
	}

	@Override
	protected void loop() {
		init();

		WatchKey key;
		try {
			key = watchService.take();

		} catch (InterruptedException | ClosedWatchServiceException x) {
			return;
		}

		Path dir = keys.get(key);
		if (dir == null) {
			return;
		}

		for (WatchEvent<?> event : key.pollEvents()) {
			try {
				processEvent(dir, event);
			} catch (Exception e) {
				Log.error("File system change processing error!", e);
			}
		}

		boolean isKeyValid = key.reset();
		if (!isKeyValid) {
			keys.remove(key);

			if (keys.isEmpty()) {
				if (!dir.toFile().exists()) {
					Log.warn("Cannot watch directory because it doesn't exist anymore", "dir", dir);
				} else {
					Log.error("Cannot watch directory due to unknown reason!", "dir", dir);
				}
			}

			watching.remove(dir.toFile().getAbsolutePath());
		}
	}

	private void processEvent(Path dir, WatchEvent<?> event) throws Exception {
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
			throw Err.notExpected();
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

	public void cancel() {
		interrupt();

		Set<WatchKey> keysToCancel = U.set(keys.keySet());
		keys.clear();

		for (WatchKey key : keysToCancel) {
			key.cancel();
		}

		try {
			watchService.close();
		} catch (IOException e) {
			Log.error("Error occurred while closing a WatchService!", e);
		}
	}

}
