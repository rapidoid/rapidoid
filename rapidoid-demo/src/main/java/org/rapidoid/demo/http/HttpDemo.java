package org.rapidoid.demo.http;

/*
 * #%L
 * rapidoid-demo
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

import java.util.concurrent.atomic.AtomicLong;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.http.HTTP;
import org.rapidoid.http.HTTPServer;
import org.rapidoid.http.Handler;
import org.rapidoid.http.HttpExchange;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class HttpDemo {

	public static void main(String[] args) {
		Conf.args(args);

		final AtomicLong n = new AtomicLong();

		HTTPServer server = HTTP.server().build();

		server.get("/hi", new Handler() {
			@Override
			public Object handle(HttpExchange x) {
				return "[" + n.incrementAndGet() + "] Hi: " + x.uri();
			}
		});

		server.start();
	}

}
