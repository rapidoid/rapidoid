package org.rapidoid.docs.eg3;

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

// Use method chaining to construct HTML tags:

import org.rapidoid.app.Apps;
import org.rapidoid.app.Screen;

public class App {

	String title = "Example 3";

	public static void main(String[] args) {
		Apps.run(args);
	}
}

class HomeScreen extends Screen {
	Object content() {
		Object link = a("Foo").href("foo.html");
		return h3("Welcome! Visit ", link);
	}
}

class FooScreen {
	String content = "At the Foo screen!";
}
