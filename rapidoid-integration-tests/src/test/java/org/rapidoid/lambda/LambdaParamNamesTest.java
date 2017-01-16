package org.rapidoid.lambda;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.domain.Movie;
import org.rapidoid.http.IsolatedIntegrationTest;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;
import org.rapidoid.setup.On;
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class LambdaParamNamesTest extends IsolatedIntegrationTest {

	@Test
	public void testLambdaParamNamesWithOutsideVariables() {
		Movie movie = new Movie("Rambo", 1990);
		int n = 123;

		ThreeParamLambda<String, Req, Integer, Resp> lambda = (Req req, Integer x, Resp resp) -> {
			return movie.getTitle() + ", " + n + ":" + x;
		};

		eq(Cls.getLambdaParameterNames(lambda), U.array("req", "x", "resp"));
	}

	@Test
	public void testLambdaParamNamesWithOnlyLocalVariables() {
		int n = 123;

		ThreeParamLambda<String, Req, Integer, Resp> lambda = (Req req, Integer x, Resp resp) -> "ok";

		eq(Cls.getLambdaParameterNames(lambda), U.array("req", "x", "resp"));
	}

	@Test
	public void testHandlerLambdaParams() {
		int a = 100;
		int b = 200;

		On.get("/").json((Req req, Integer x, Resp resp) -> U.join(":", a, b, x));

		onlyGet("/?x=10&b=33");
	}

	@Test
	public void testLambdaParams1() {
		On.get("/foo").json((Double a) -> a);

		onlyGet("/foo?a=10");
	}

	@Test
	public void testLambdaParams2() {
		On.get("/foo").json((Double a, Double b) -> U.join(":", a, b));

		onlyGet("/foo?a=10&b=20");
	}

	@Test
	public void testLambdaParams3() {
		On.get("/foo").json((Double a, Double b, Double c) -> U.join(":", a, b, c));

		onlyGet("/foo?a=10&b=20&c=30");
	}

	@Test
	public void testLambdaParams4() {
		On.get("/foo").json((Double a, Double b, Double c, Double d) -> U.join(":", a, b, c, d));

		onlyGet("/foo?a=10&b=20&c=30&d=40");
	}

	@Test
	public void testLambdaParams5() {
		On.get("/foo").json((Double a, Double b, Double c, Double d, Double e) -> U.join(":", a, b, c, d, e));

		onlyGet("/foo?a=10&b=20&c=30&d=40&e=50");
	}

}
