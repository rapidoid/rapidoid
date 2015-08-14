package org.rapidoid.docs.eg001;

import org.rapidoid.annotation.Page;
import org.rapidoid.annotation.Web;
import org.rapidoid.main.Rapidoid;
import org.rapidoid.util.U;

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

// Hello, web pages! :: Let's start Rapidoid and implement some web pages:

@Web
public class Main {

	public static void main(String[] args) {
		Rapidoid.run(args);
	}

	@Page("/")
	public String hello() {
		return "Hello, world!";
	}

	@Page(title = "Saying 'hi'")
	public String hi(String name) {
		return U.format("Hi, %s!", name);
	}

	@Page(raw = true)
	public Object simple() {
		return "<p><b>RAW</b> HTML!<p>";
	}

}
