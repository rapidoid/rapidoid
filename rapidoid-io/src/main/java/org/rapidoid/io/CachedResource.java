package org.rapidoid.io;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.util.concurrent.ConcurrentMap;

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
public class CachedResource {

	private static final ConcurrentMap<String, CachedResource> FILES = U.concurrentMap();

	private final String name;

	private volatile byte[] bytes;

	private volatile long lastUpdatedOn;

	private volatile long lastModified;

	private volatile String content;

	public CachedResource(String name) {
		this.name = name;
	}

	public static CachedResource from(String filename) {
		CachedResource cachedFile = FILES.get(filename);

		if (cachedFile == null) {
			cachedFile = new CachedResource(filename);

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
		if (U.time() - lastUpdatedOn >= 1000) {
			load(name);

			if (bytes == null) {
				// if the resource doesn't exist, try loading the default resource
				load(IO.getDefaultFilename(name));
			}
		}
	}

	protected void invalidate() {
		content = null;
		lastUpdatedOn = U.time();
	}

	protected void load(String filename) {
		File file = new File(filename);

		if (file.exists()) {
			// a normal file on the file system
			if (file.lastModified() > this.lastModified) {
				Log.debug("Reloading file", "name", filename);
				this.lastModified = file.lastModified();
				this.bytes = IO.loadBytes(filename);
				invalidate();
			}
		} else {
			// it might not exist or it might be on the classpath or compressed in a JAR
			Log.debug("Reloading classpath resource", "name", filename);
			this.bytes = IO.loadBytes(filename);
			invalidate();
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

}
