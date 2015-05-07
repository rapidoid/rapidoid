package org.rapidoidx.demo.taskplanner;

/*
 * #%L
 * rapidoid-demo
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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
import org.rapidoid.app.Apps;
import org.rapidoidx.db.XDB;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class Main {

	public static void main(String[] args) {
		Apps.run("oauth-no-state");
		XDB.clear();
		for (int i = 0; i < 100; i++) {
			XDB.init("task title=?, description=?, rating=?", "abc" + i, "Some description of the task " + i, i * 10);
		}
	}

}
