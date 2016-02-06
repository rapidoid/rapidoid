package org.rapidoid.http;

/*
 * #%L
 * rapidoid-integration-tests
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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.fast.On;
import org.rapidoid.u.U;
import org.rapidoid.wire.Wire;

import javax.annotation.Generated;
import java.lang.annotation.Retention;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Authors("Nikolche Mihajlovski")
@Since("5.0.10")
public class HttpPojoControllerTest extends HttpTestCommons {

	@Test
	public void testPojoHandlers() {
		On.get("/a").controllers(new Object() {

			@GET(uri = "/a")
			public Object theFoo() {
				return "foo";
			}

			@GET(uri = "/x")
			public Object x() {
				return "x";
			}
		});

		onlyGet("/a");
		notFound("/b");
		notFound("/x");

		List<Class<?>> ctrls = On.annotated(MyController.class).in("pkg1", "pkg2").getAll();
		isTrue(ctrls.isEmpty());

		List<Class<?>> ctrls2 = On.annotated(MyController.class).getAll();
		eq(ctrls2, U.list(Ff.class));

		On.annotated(MyController.class, MyController.class).forEach(On::req);

		// cls -> On.req(Wire.singleton(cls))
		onlyGet("/a");
		onlyGet("/b");
		notFound("/x");
	}

	@Test
	public void testPojoHandlersWithIoC() {
		notFound("/b");

		List<Class<?>> ctrls = On.annotated(MyController.class).in("pkg1", "pkg2").getAll();
		isTrue(ctrls.isEmpty());

		List<Class<?>> ctrls2 = On.annotated(MyController.class, Generated.class).getAll();
		eq(ctrls2, U.list(Ff.class));

		On.annotated(MyController.class, MyController.class).forEach(cls -> On.req(Wire.singleton(cls)));

		onlyGet("/b");
		notFound("/x");
	}

}

@Retention(RUNTIME)
@interface MyController {
}

@MyController
class Ff {

	@GET(uri = "/b")
	public Object bbb() {
		return "bar";
	}

}
