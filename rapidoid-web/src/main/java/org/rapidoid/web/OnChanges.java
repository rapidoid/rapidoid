package org.rapidoid.web;

/*
 * #%L
 * rapidoid-web
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.io.watch.FilesystemChangeListener;
import org.rapidoid.io.watch.Watch;
import org.rapidoid.log.Log;
import org.rapidoid.scan.ClasspathUtil;

import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class OnChanges {

	static final OnChanges INSTANCE = new OnChanges();

	static volatile boolean initialized;

	private OnChanges() {
	}

	public synchronized void restart() {
		if (!initialized) {
			initialized = true;
			if (Conf.dev()) {
				Set<String> classpathFolders = ClasspathUtil.getClasspathFolders();
				Log.info("Watching classpath for changes...", "classpath", classpathFolders);

				Watch.dirs(classpathFolders, new FilesystemChangeListener() {
					@Override
					public void created(String filename) {
						markAsDirty();
					}

					@Override
					public void modified(String filename) {
						markAsDirty();
					}

					@Override
					public void deleted(String filename) {
						markAsDirty();
					}
				});

			} else {
				Log.warn("Not running in dev mode, hot class reloading is disabled!");
			}
		}
	}

	private void markAsDirty() {
		Setup.notifyChanges();
	}

}
