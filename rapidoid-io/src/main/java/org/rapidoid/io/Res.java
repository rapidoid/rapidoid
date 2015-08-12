package org.rapidoid.io;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.util.U;

/*
 * #%L
 * rapidoid-io
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

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class Res {

	private static final ConcurrentMap<String, Res> FILES = U.concurrentMap();

	public static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(1);

	private final String name;

	private volatile byte[] bytes;

	private volatile long lastUpdatedOn;

	private volatile long lastModified;

	private volatile String content;

	private volatile boolean trackingChanges;

	private volatile String cachedFileName;

	private final List<Runnable> changeListeners = U.synchronizedList();

	private Res(String name) {
		this.name = name;
	}

	public static Res from(String filename) {
		Res cachedFile = FILES.get(filename);

		if (cachedFile == null) {
			cachedFile = new Res(filename);

			if (FILES.size() < 1000) {
				FILES.putIfAbsent(filename, cachedFile);
			}
		}

		return cachedFile;
	}

	public synchronized byte[] getBytes() {
		loadResource();

		U.must(exists(), "The resource %s doesn't exist!", name);
		return bytes;
	}

	protected void loadResource() {
		// micro-caching the file content, expires after 1 second
		if (U.time() - lastUpdatedOn >= 500) {
			boolean hasChanged = false;

			synchronized (this) {

				byte[] oldBytes = bytes;

				byte[] res = load(name);

				if (res == null) {
					String defaultFilename = IO.getDefaultFilename(name);
					Log.debug("Trying to load the default resource", "name", defaultFilename);
					// if the resource doesn't exist, try loading the default resource
					res = load(defaultFilename);
				}

				this.bytes = res;

				hasChanged = !U.eq(oldBytes, bytes)
						&& (oldBytes == null || bytes == null || !Arrays.equals(oldBytes, bytes));

				lastUpdatedOn = U.time();
				if (hasChanged) {
					content = null;
				}
			}

			if (hasChanged) {
				notifyChangeListeners();
			}
		}
	}

	protected byte[] load(String filename) {
		File file = IO.file(filename);

		if (file.exists()) {

			// a normal file on the file system
			Log.debug("File exists", "file", file);

			if (file.lastModified() > this.lastModified || !filename.equals(cachedFileName)) {
				Log.debug("Reloading file", "file", file);
				this.lastModified = file.lastModified();
				this.cachedFileName = filename;
				return IO.loadBytes(filename);
			} else {
				Log.debug("File not modified", "file", file);
				return bytes;
			}
		} else {
			// it might not exist or it might be on the classpath or compressed in a JAR
			Log.debug("Trying to load classpath resource", "file", file);
			byte[] res = IO.loadBytes(filename);
			this.cachedFileName = (res != null) ? filename : null;
			return res;
		}
	}

	public synchronized String getContent() {
		U.must(exists(), "The resource %s doesn't exist!", name);

		if (content == null) {
			byte[] b = getBytes();
			content = b != null ? new String(b) : null;
		}

		return content;
	}

	public boolean exists() {
		loadResource();
		return bytes != null;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Res(" + name + ")";
	}

	public synchronized Reader getReader() {
		U.must(exists(), "The resource %s doesn't exist!", name);
		return new StringReader(getContent());
	}

	public List<Runnable> getChangeListeners() {
		return changeListeners;
	}

	private void notifyChangeListeners() {
		if (!changeListeners.isEmpty()) {
			Log.info("Resource has changed, reloading...", "name", name);
		}

		for (Runnable listener : changeListeners) {
			try {
				listener.run();
			} catch (Throwable e) {
				Log.error("Error while processing resource changes!", e);
			}
		}
	}

	public synchronized Res trackChanges() {
		if (!trackingChanges) {
			this.trackingChanges = true;
			EXECUTOR.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					// loading the resource causes the resource to check for changes
					loadResource();
				}
			}, 0, 300, TimeUnit.MILLISECONDS);
		}

		return this;
	}

}
