package org.rapidoid.io.watch;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;

import java.util.Queue;

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
public class FilesystemChangeQueueListener extends RapidoidThing implements FilesystemChangeListener {

	private final Queue<String> created;
	private final Queue<String> modified;
	private final Queue<String> deleted;

	public FilesystemChangeQueueListener(Queue<String> created, Queue<String> modified, Queue<String> deleted) {
		this.created = created;
		this.modified = modified;
		this.deleted = deleted;
	}

	@Override
	public void modified(String filename) {
		Log.debug("A file was modified", "file", filename);
		modified.add(filename);
	}

	@Override
	public void deleted(String filename) {
		Log.debug("A file was deleted", "file", filename);
		deleted.add(filename);
	}

	@Override
	public void created(String filename) {
		Log.debug("A file was created", "file", filename);
		created.add(filename);
	}

}
