package org.rapidoid.demo.pojo;

/*
 * #%L
 * rapidoid-demo
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import org.rapidoid.pojo.POJO;
import org.rapidoid.pojo.PojoDispatcher;
import org.rapidoid.pojo.PojoRequest;
import org.rapidoid.pojo.impl.PojoRequestImpl;
import org.rapidoid.util.JSON;
import org.rapidoid.util.U;

public class CLIPojoDemo {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Throwable {
		U.args(args);

		PojoDispatcher dispatcher = POJO.serviceDispatcher();

		System.out.println("HELLO");

		U.setLogLevel(U.DEBUG);

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String line;
		while ((line = reader.readLine()) != null) {
			int p1 = line.indexOf(' ');
			int p2 = line.indexOf(' ', p1 + 1);
			Map<String, String> extra = p2 > 0 ? JSON.parse(line.substring(p2 + 1), Map.class) : U.map();
			PojoRequest req = new PojoRequestImpl(line.substring(0, p1), p2 > 0 ? line.substring(p1 + 1, p2)
					: line.substring(p1 + 1), extra);
			U.debug("processing request", "request", req);
			System.out.println(dispatcher.dispatch(req));
		}

		System.out.println("BYE");
	}

}
