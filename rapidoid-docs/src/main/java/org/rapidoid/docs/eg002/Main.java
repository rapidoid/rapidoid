package org.rapidoid.docs.eg002;

import java.util.Map;

import org.rapidoid.annotation.GET;
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

// Hello, RESTful services! :: Let's start Rapidoid and implement a RESTful service:

@Web
public class Main {

	public static void main(String[] args) {
		Rapidoid.run(args);
	}

	@GET
	public Map<String, ?> upper(String s) {
		String up = s.toUpperCase();
		int len = s.length();
		return U.map("src", s, "upper", up, "length", len);
	}

}
