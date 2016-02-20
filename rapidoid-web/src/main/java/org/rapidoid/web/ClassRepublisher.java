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
import org.rapidoid.commons.Deep;
import org.rapidoid.io.watch.ClassRefresher;
import org.rapidoid.ioc.IoCContext;
import org.rapidoid.ioc.IoCContextChanges;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.UTILS;

import java.util.Collection;
import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class ClassRepublisher implements ClassRefresher {

	private final Setup setup;

	public ClassRepublisher(Setup setup) {
		this.setup = setup;
	}

	@Override
	public void refresh(List<Class<?>> reloaded, List<String> deleted) {
		Log.info("-------------------------------------------------------------------");
		Collection<Class<?>> reloadedInfo = Deep.copyOf(reloaded, UTILS.TRANSFORM_TO_SIMPLE_CLASS_NAME);
		Log.info("Refreshed classes", "reloaded", reloadedInfo, "deleted", "[" + U.join(", ", simpleNames(deleted)) + "]");


		}


		Log.info("Completed class republishing", "context", context);
	}

	private List<String> simpleNames(List<String> classNames) {
		List<String> simpleNames = U.list();

		for (String name : classNames) {
			simpleNames.add(U.last(name.split("\\.")));
		}

		return simpleNames;
	}

}
