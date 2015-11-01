package org.rapidoid.http;

/*
 * #%L
 * rapidoid-rest
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

import java.util.Map;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.concurrent.Promise;
import org.rapidoid.concurrent.Promises;
import org.rapidoid.http.fast.On;
import org.rapidoid.http.fast.ParamHandler;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("4.4.0")
public class DynamicClientTest extends TestCommons {

	private final MyClient client = REST.client(MyClient.class);

	@Test
	public void testDynamic() {
		On.address("127.0.0.1").port(8989).get("/test-abc").html("abc-ok");

		On.get("/nums").json("[1, 2, 3]");

		On.get("/size").json(new ParamHandler() {
			@Override
			public Object handle(Map<String, Object> params) throws Exception {
				return params.get("s").toString().length();
			}
		});

		On.post("/echo").json(new ParamHandler() {
			@Override
			public Object handle(Map<String, Object> params) throws Exception {
				return params;
			}
		});

		eq(client.abc(), "abc-ok");
		eq(client.numbers(), U.list(1, 2, 3));
		eq(client.sizeOf("abcde"), 5);

		Promise<Integer> cb = Promises.create();
		client.asyncSizeOf("four", cb);
		eq(cb.get().intValue(), 4);

		MyBean bean = client.theBean(123, "xy", true);
		eq(bean.aa, "123");
		eq(bean.bb, "xy");
		eq(bean.cc, true);

		Promise<MyBean> beanCb = Promises.create();
		client.asyncBean(456, "cool", false, beanCb);
		MyBean bean2 = beanCb.get();
		eq(bean2.aa, "456");
		eq(bean2.bb, "cool");
		eq(bean2.cc, false);
	}

}
