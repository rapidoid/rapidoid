package org.rapidoid.io.watch;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.config.RapidoidInitializer;
import org.rapidoid.io.Res;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
public class Dir extends RapidoidInitializer implements FilesystemChangeListener {

	private static final ScheduledExecutorService EXECUTORS = Executors.newScheduledThreadPool(8);

	private static final Map<String, Dir> DIRS = Coll.autoExpandingMap(new Mapper<String, Dir>() {
		@Override
		public Dir map(String path) throws Exception {
			return new Dir(path);
		}
	});

	private final String path;

	private final File dir;

	private final Set<Res> files = U.set();

	private final Set<String> folders = U.set();

	private boolean dirty = true;

	public static Dir from(String path) {
		return DIRS.get(path);
	}

	private Dir(String path) {
		this.path = path;
		this.dir = new File(path);
		Watch.dir(path, this);
		refresh();
	}

	@Override
	public synchronized String toString() {
		return U.frmt("Dir(%s, %d files, %d folders)", path, files.size(), folders.size());
	}

	@Override
	public void created(String filename) {
		refreshLater();
	}

	@Override
	public void modified(String filename) {
		refreshLater();
	}

	@Override
	public void deleted(String filename) {
		refreshLater();
	}

	public synchronized void refresh() {
		if (!dirty) {
			return;
		}
		Log.info("Refreshing dir", "path", path);

		files.clear();
		folders.clear();

		traverse(Paths.get(path), new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				files.add(Res.from(file.toFile()));
				return super.visitFile(file, attrs);
			}

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				String dirPath = dir.toAbsolutePath().toString();
				if (!dirPath.equals(path)) {
					folders.add(dirPath);
				}
				return super.preVisitDirectory(dir, attrs);
			}
		});

		Log.info("Refreshed folder content", "files", files, "folders", folders);

		dirty = false;
	}

	public synchronized Set<Res> files() {
		return files;
	}

	public synchronized Set<String> folders() {
		return folders;
	}

	private synchronized void refreshLater() {
		dirty = true;

		EXECUTORS.schedule(new Runnable() {
			@Override
			public void run() {
				refresh();
			}
		}, 500, TimeUnit.MILLISECONDS);
	}

	public synchronized boolean exists() {
		return dir.exists();
	}

	public static void traverse(final Path root, FileVisitor<Path> visitor) {
		try {
			Files.walkFileTree(root, visitor);
		} catch (IOException e) {
			Log.error("Error occurred while traversing the directory tree: " + root);
		}
	}

}
