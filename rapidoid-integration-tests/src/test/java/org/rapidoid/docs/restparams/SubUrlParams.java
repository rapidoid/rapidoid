package org.rapidoid.docs.restparams;

import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.POST;
import org.rapidoid.u.U;

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

@Controller
public class SubUrlParams {

	@GET("/hey/{name}/{age:\\d+}")
	public String hey(String name, int age) {
		return U.frmt("Hey %s (%s)", name, age);
	}

	@POST("/size/{s}")
	public int size(String s) {
		return s.length();
	}

}
