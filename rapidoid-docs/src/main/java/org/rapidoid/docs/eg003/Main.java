package org.rapidoid.docs.eg003;

import java.util.List;
import java.util.Map;

import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.POST;
import org.rapidoid.annotation.Param;
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

// More RESTful services :: Let's create more RESTful services:

@Web
public class Main {

	public static void main(String[] args) {
		Rapidoid.run(args);
	}

	@GET
	public String hey(String name, int age) {
		return U.format("Hey %s (%s)", name, age);
	}

	@GET("/hello")
	public String hey2(@Param("name") String s, @Param("age") int years) {
		return "Hey " + s + " (" + years + ")";
	}

	@POST
	public List<String> foo(List<String> params) {
		return params;
	}

	@GET("/barbar")
	public Map<String, Object> bar(Map<String, Object> params) {
		return params;
	}

}
