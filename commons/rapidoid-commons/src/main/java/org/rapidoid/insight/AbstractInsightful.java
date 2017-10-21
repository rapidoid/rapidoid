package org.rapidoid.insight;

import org.rapidoid.RapidoidThing;
import org.rapidoid.log.Log;

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

/**
 * @author Nikolche Mihajlovski
 * @since 4.1.0
 */
public abstract class AbstractInsightful extends RapidoidThing implements Insightful {

	private final String kind;

	private final String name;

	private final Thread creatorThread;

	public AbstractInsightful(String kind, String name) {
		this.creatorThread = Thread.currentThread();
		this.kind = kind;
		this.name = name;

		Log.debug("Creating object", "kind", kind, "name", name, "creatorThread", creatorThread.getName(), "class",
			getClass().getSimpleName());

		Insights.register(this);
	}

	@Override
	public String getKind() {
		return kind;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Thread getCreatorThread() {
		return creatorThread;
	}

}
