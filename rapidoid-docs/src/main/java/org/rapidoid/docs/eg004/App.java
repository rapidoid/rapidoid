package org.rapidoid.docs.eg004;

import org.rapidoid.quick.Quick;

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

// Rapidoid has built-in themes! :: Let's use the built-in theme 2:

public class App {
	String title = "Example 4";
	String content = "Fancy theme!";
	String theme = "2"; // here

	public static void main(String[] args) {
		Quick.run(args);
	}
}

class FooScreen {}

class BarScreen {}
