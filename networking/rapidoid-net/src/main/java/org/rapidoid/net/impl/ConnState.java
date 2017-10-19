package org.rapidoid.net.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.Resetable;

import java.util.List;

/*
 * #%L
 * rapidoid-net
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
@Since("2.0.0")
public class ConnState extends RapidoidThing implements Resetable {

	public volatile long n;

	public volatile Object obj;

	/* COMMENTED OUT FOR PRODUCTION (the log is used for debugging during development): */

	// private final List<String> log = Collections.synchronizedList(U.<String> list());

	public void reset() {
		n = 0;
		obj = null;
		log("<<< RESET >>>");
	}

	/* The log is used for debugging during development. */
	public void log(String msg) {
		/* COMMENTED OUT FOR PRODUCTION: */
		// log.add(msg);
	}

	/* The log is used for debugging during development. */
	public List<String> log() {
		return null;

		/* COMMENTED OUT FOR PRODUCTION: */
		// return log;
	}

}
