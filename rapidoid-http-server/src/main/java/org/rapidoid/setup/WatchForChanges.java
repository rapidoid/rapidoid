package org.rapidoid.setup;

import org.rapidoid.RapidoidThing;
import org.rapidoid.io.watch.FilesystemChangeListener;
import org.rapidoid.io.watch.Watch;
import org.rapidoid.log.Log;
import org.rapidoid.scan.ClasspathUtil;

import java.util.Set;

/*
 * #%L
 * rapidoid-http-server
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

public class WatchForChanges extends RapidoidThing {

	public static void activate() {
		Set<String> classpathFolders = ClasspathUtil.getClasspathFolders();
		Log.info("!Watching classpath for changes...", "classpath", classpathFolders);

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
	}

	private static void markAsDirty() {
		App.notifyChanges();
	}

}
