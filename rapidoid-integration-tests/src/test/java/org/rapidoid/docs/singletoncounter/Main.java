package org.rapidoid.docs.singletoncounter;

import org.rapidoid.annotation.Controller;
import org.rapidoid.setup.App;

import javax.inject.Inject;

/*
 * #%L
 * rapidoid-integration-tests
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

// Dependency injection of singletons :: Injecting a singleton 

public class Main {
	String title = "Singleton counter";
	String theme = "2";

	public static void main(String[] args) {
		App.bootstrap(args);
	}
}

@Controller
class HomeScreen {
	@Inject // here
		Counter c; // here

	Object content() {
		return c.get();
	}
}

@Controller
class OtherScreen {
	@Inject // here
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
