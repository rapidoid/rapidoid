package org.rapidoid.io;

import java.io.File;
import java.util.concurrent.ConcurrentMap;

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

/**
 * @author Nikolche Mihajlovski
 * @since 4.1.0
 */
public class CachedResource {

	private static final ConcurrentMap<String, CachedResource> FILES = U.concurrentMap();

	private final String filename;

	private volatile byte[] bytes;

	private volatile long lastUpdatedOn;

	private volatile long lastModified;

	public CachedResource(String filename) {
		this.filename = filename;
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

	public byte[] getContent() {
		// micro-caching the file content, expires after 1 second
		if (U.time() - lastUpdatedOn >= 1000) {
			File file = new File(filename);

			// a normal file on the file system
			if (file.exists() && file.lastModified() > this.lastModified) {
				this.lastModified = file.lastModified();
				bytes = IO.loadBytes(filename);
			} else {
				// it might not exist or it might be on the classpath or compressed in a JAR
				if (bytes == null) {
					bytes = IO.loadBytes(filename);
				}
			}

			lastUpdatedOn = U.time();
		}

		return bytes;
	}

	public boolean exists() {
		return getContent() != null;
	}

	public String getFilename() {
		return filename;
	}

}
