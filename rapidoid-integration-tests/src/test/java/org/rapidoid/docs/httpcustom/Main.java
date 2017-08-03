package org.rapidoid.docs.httpcustom;

import org.rapidoid.config.Conf;
import org.rapidoid.setup.Admin;
import org.rapidoid.setup.App;
import org.rapidoid.setup.AppBootstrap;
import org.rapidoid.setup.On;

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

public class Main {

	public static void main(String[] args) {

		// first thing to do - initializing Rapidoid, without bootstrapping anything at the moment
		AppBootstrap bootstrap = App.run(args); // instead of App.bootstrap(args), which might start the server

		// customizing the server address and port - before the server is bootstrapped
		On.address("0.0.0.0").port(9998);
		Admin.address("127.0.0.1").port(9999);

		// fine-tuning the HTTP server
		Conf.HTTP.set("maxPipeline", 32);
		Conf.NET.set("bufSizeKB", 16);

		// now bootstrap some components, e.g. built-in services, classpath scanning (beans), JMX, Admin Center
		bootstrap.services().beans().jmx().adminCenter();

		// continue with normal setup
		On.get("/x").json("x");
	}

}
