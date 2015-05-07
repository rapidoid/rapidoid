package org.rapidoid.docs.eg021;

import org.rapidoid.annotation.Inject;
import org.rapidoid.app.Apps;

/*
 * #%L
 * rapidoid-docs
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

// Dependency injection of singletons :: Injecting a singleton 

public class App {
	String title = "Singleton counter";
	String theme = "2";

	public static void main(String[] args) {
		Apps.run(args);
	}
}

class HomeScreen {
	@Inject
	// here
	Counter c; // here

	Object content() {
		return c.get();
	}
}

class OtherScreen {
	@Inject
	// here
	Counter c; // here

	Object content() {
		return c.get();
	}
}

class Counter { // here
	private int n = 0;

	synchronized int get() {
		return ++n;
	}
}
