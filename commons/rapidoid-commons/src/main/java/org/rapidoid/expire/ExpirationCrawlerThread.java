package org.rapidoid.expire;

/*
 * #%L
 * rapidoid-commons
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

import org.rapidoid.activity.RapidoidThread;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.1.7")
public class ExpirationCrawlerThread extends RapidoidThread {

	private final int resolution;

	@SuppressWarnings("unchecked")
	private final List<Iterable<? extends Expiring>> collections = Coll.synchronizedList();

	public ExpirationCrawlerThread(String name, int resolution) {
		super(name);
		this.resolution = resolution;
		setPriority(Thread.MIN_PRIORITY);
		setDaemon(true);
	}

	public void register(Iterable<? extends Expiring> collection) {
		collections.add(collection);
	}

	public void deregister(Iterable<? extends Expiring> collection) {
		collections.remove(collection);
	}

	@Override
	public void run() {
		long startedAt = U.time();

		while (!Thread.interrupted()) {

			long timeSpent = U.time() - startedAt;
			U.sleep(Math.max(resolution - timeSpent, 0));
			startedAt = U.time();

			crawl();
		}
	}

	private void crawl() {
		for (Iterable<? extends Expiring> coll : Coll.copyOf(collections)) {
			long now = U.time();

			for (Expiring target : coll) {

				try {
					long expiresAt = target.getExpiresAt();

					if (expiresAt > 0 && expiresAt < now) {
						target.expire();
						target.setExpiresAt(0);
					}

				} catch (Exception e) {
					Log.error("Error on expiration!", e);
				}
			}
		}
	}

}
