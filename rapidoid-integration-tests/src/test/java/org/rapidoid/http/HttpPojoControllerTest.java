package org.rapidoid.http;

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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.POST;
import org.rapidoid.annotation.Since;
import org.rapidoid.ioc.IoC;
import org.rapidoid.scan.Scan;
import org.rapidoid.setup.App;
import org.rapidoid.u.U;

import javax.annotation.Generated;
import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.0.10")
public class HttpPojoControllerTest extends IsolatedIntegrationTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testPojoHandlers() {
		App.beans(new Object() {

			@GET(uri = "/a")
			public Object theFoo() {
				return "foo";
			}

			@POST(uri = "/x")
			public Object x(Req req, Resp resp) {
				return "x";
			}
		});

		onlyGet("/a");
		onlyPost("/x");
		notFound("/b");

		List<String> ctrls = Scan.annotated(MyTestController.class).in("pkg1", "pkg2").getAll();
		isTrue(ctrls.isEmpty());

		List<String> ctrls2 = Scan.annotated(MyTestController.class).in("non-existing-pkg", "").getAll();
		eq(ctrls2, U.list(Ff.class.getName()));

		Scan.annotated(MyTestController.class, MyTestController.class).in(App.path()).forEach(App::beans);

		onlyGet("/a");
		onlyGet("/b");
		onlyPost("/x");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPojoHandlersWithIoC() {
		notFound("/b");

		List<String> ctrls = Scan.annotated(MyTestController.class).in(U.list("pkg1", "pkg2")).getAll();
		isTrue(ctrls.isEmpty());

		List<String> ctrls2 = Scan.annotated(MyTestController.class, Generated.class).in(App.path()).getAll();
		eq(ctrls2, U.list(Ff.class.getName()));

		Scan.annotated(MyTestController.class, MyTestController.class).in(App.path()).forEach(cls -> App.beans(IoC.singleton(cls)));

		onlyGet("/b");
		onlyGet("/x");
		notFound("/x");
	}

}

@interface MyTestController {
}

@MyTestController
class Ff {

	@GET("/b")
	public Object bbb() {
		return "bar";
	}

}
