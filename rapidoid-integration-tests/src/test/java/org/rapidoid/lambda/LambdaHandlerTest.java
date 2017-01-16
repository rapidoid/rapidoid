package org.rapidoid.lambda;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.IsolatedIntegrationTest;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class LambdaHandlerTest extends IsolatedIntegrationTest {

	@Test
	public void testLambdaHandlerWithAnonymousClass() {
		TwoParamLambda<Resp, Req, Integer> oneParamLambda = new TwoParamLambda<Resp, Req, Integer>() {
			@Override
			public Resp execute(Req param, Integer x) throws Exception {
				return param.response().result("x=" + x);
			}
		};

		On.get("/test").json(oneParamLambda);

		onlyGet("/test?x=123");
	}

	@Test
	public void testLambdaHandlerWithLambda() {
		TwoParamLambda<Resp, Req, Integer> oneParamLambda = (Req param, Integer y) -> param.response().result("y=" + y);

		On.get("/test").html(oneParamLambda);

		onlyGet("/test?y=456");
	}

}
