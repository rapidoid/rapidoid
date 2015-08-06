package org.rapidoid.docs.eg002;

import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.Web;
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

// Hello, world 2! :: Let's start Rapidoid:

@Web
public class Main {

	public static void main(String[] args) {
		Rapidoid.run(args);
	}

	@GET
	public String upper(String s) {
		return s.toUpperCase();
	}
}

@Web
class Opa {
	@GET
	public String iha() {
		return "jee";
	}
}
