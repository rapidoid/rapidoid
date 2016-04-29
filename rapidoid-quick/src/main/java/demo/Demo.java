package demo;

/*
 * #%L
 * rapidoid-quick
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.Page;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;
import org.rapidoid.io.IO;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;
import org.rapidoid.web.Rapidoid;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Demo extends RapidoidThing {

	public static void main(String[] args) {
		ClasspathUtil.appJar("/tmp/app.jar");
		Rapidoid.run(args);

		On.get("/").plain("Hello, world!");

		On.beans(new Object() {

			@Page("/hey")
			public Object home(Req req) {
				return U.list(GUI.grid(req.headers()), GUI.grid(req.data()));
			}

			@GET
			public Object _ping(Req req, Resp resp) {
				IO.write(resp.out(), "PONG");
				IO.write(resp.out(), "!");
				return req;
			}
		});
	}

}
