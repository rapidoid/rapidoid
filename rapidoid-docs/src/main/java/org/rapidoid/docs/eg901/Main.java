package org.rapidoid.docs.eg901;

import org.rapidoid.annotation.App;
import org.rapidoid.main.Rapidoid;

/*
 * #%L
 * rapidoid-docs
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

// Hide the navigation bar extras :: No search or login extras in navigation

@App
public class Main {
	String title = "No extras here";
	Object content = "Hello!";
	String theme = "4";

	boolean full = false; // here

	public static void main(String[] args) {
		Rapidoid.run(args);
	}
}
