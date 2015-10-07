package org.rapidoid.docs.lowsimple;

import java.util.Map;

import org.rapidoid.http.On;
import org.rapidoid.http.ParamHandler;
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

public class Main {
	public static void main(String[] args) {
		On.get("/").html("Hello world!");

		On.post("/upper").json(new ParamHandler() {
			@Override
			public Object handle(Map<String, Object> params) {
				String s = String.valueOf(params.get("s"));
				return U.map("src", s, "upper", s.toUpperCase());
			}
		});

		On.listen(8080);
	}
}
