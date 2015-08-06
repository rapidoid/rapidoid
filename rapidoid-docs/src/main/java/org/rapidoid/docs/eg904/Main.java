package org.rapidoid.docs.eg904;

import org.rapidoid.annotation.App;
import org.rapidoid.annotation.Web;
import org.rapidoid.app.GUI;
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

// Configure screens to be displayed :: Show only the Ab and Cd screens

@App
public class Main {
	String title = "My app";
	Object content = "Hello!";
	String theme = "5";

	Object[] screens = { "ab", CdScreen.class }; // here

	public static void main(String[] args) {
		Rapidoid.run(args);
	}
}

@Web
class CdScreen extends GUI {}

@Web
class BarScreen extends GUI {} // here

@Web
class AbScreen extends GUI {}
