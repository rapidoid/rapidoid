package org.rapidoid.io;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

/*
 * #%L
 * rapidoid-commons
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

	private static final ConcurrentMap<Set<String>, Res> FILES = U.concurrentMap();

	public static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(1);

	private final String shortName;

	private final Set<String> filenames;

	private volatile byte[] bytes;

	private volatile long lastUpdatedOn;

	private volatile long lastModified;

	private volatile String content;

	private volatile boolean trackingChanges;

	private volatile String cachedFileName;

	private volatile Object attachment;

	private final Map<String, Runnable> changeListeners = U.synchronizedMap();

	private Res(String shortName, Set<String> filenames) {
		this.shortName = shortName;
		this.filenames = filenames;
	}

	public static Res from(String filename) {
		return from(filename, true, filename);
	}

	public static Res from(File file) {
		return from(file.getAbsolutePath());
	}

	public static Res from(String shortName, boolean withDefaults, String... filenames) {
		Set<String> fnames = U.set(filenames);

		if (withDefaults) {
			for (String filename : filenames) {
				fnames.add(IO.getDefaultFilename(filename));
			}
		}

		U.must(!fnames.isEmpty(), "Resource filename(s) must be specified!");

		Res cachedFile = FILES.get(fnames);

		if (cachedFile == null) {
			cachedFile = new Res(shortName, fnames);

			if (FILES.size() < 1000) {
				FILES.putIfAbsent(fnames, cachedFile);
			}
		}

		return cachedFile;
	}

	public synchronized byte[] getBytes() {
		loadResource();

		mustExist();
		return bytes;
	}

	public byte[] getBytesOrNull() {
		loadResource();
		return bytes;
	}

	protected void loadResource() {
		// micro-caching the file content, expires after 500ms
		if (U.time() - lastUpdatedOn >= 500) {
			boolean hasChanged = false;

			synchronized (this) {

				byte[] old = bytes;
				byte[] foundRes = null;

				for (String filename : filenames) {
					Log.trace("Trying to load the resource", "name", shortName, "file", filename);
					byte[] res = load(filename);
					if (res != null) {
						Log.trace("Loaded the resource", "name", shortName, "file", filename);
						foundRes = res;
						this.cachedFileName = filename;
						break;
					}
				}

				if (foundRes == null) {
					this.cachedFileName = null;
				}

				this.bytes = foundRes;
				hasChanged = !U.eq(old, bytes) && (old == null || bytes == null || !Arrays.equals(old, bytes));
				lastUpdatedOn = U.time();

				if (hasChanged) {
					content = null;
					attachment = null;
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
			Log.trace("Resource file exists", "name", shortName, "file", file);

			if (file.lastModified() > this.lastModified || !filename.equals(cachedFileName)) {
				Log.info("Loading resource file", "name", shortName, "file", file);
				this.lastModified = file.lastModified();
				return IO.loadBytes(filename);
			} else {
				Log.trace("Resource file not modified", "name", shortName, "file", file);
				return bytes;
			}
		} else {
			// it might not exist or it might be on the classpath or compressed in a JAR
			Log.trace("Trying to load classpath resource", "name", shortName, "file", file);
			return IO.loadBytes(filename);
		}
	}

	public synchronized String getContent() {
		mustExist();

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

	public String getShortName() {
		return shortName;
	}

	@Override
	public String toString() {
		return "Res(" + shortName + ")";
	}

	public synchronized Reader getReader() {
		mustExist();
		return new StringReader(getContent());
	}

	public void mustExist() {
		U.must(exists(), "The file '%s' doesn't exist! Path: %s", shortName, filenames);
	}

	public Res onChange(Runnable listener) {
		return onChange("", listener);
	}

	public Res onChange(String name, Runnable listener) {
		changeListeners.put(name, listener);
		return this;
	}

	private void notifyChangeListeners() {
		if (!changeListeners.isEmpty()) {
			Log.info("Resource has changed, reloading...", "name", shortName);
		}

		for (Runnable listener : changeListeners.values()) {
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

	@SuppressWarnings("unchecked")
	public <T> T attachment() {
		return exists() ? (T) attachment : null;
	}

	public void attach(Object attachment) {
		this.attachment = attachment;
	}

}
