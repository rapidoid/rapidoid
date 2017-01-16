package org.rapidoid.http;

/*
 * #%L
 * rapidoid-web
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
import org.rapidoid.annotation.Since;
import org.rapidoid.http.customize.ErrorHandler;
import org.rapidoid.http.customize.defaults.Defaults;
import org.rapidoid.setup.On;
import org.rapidoid.test.ExpectErrors;

@Authors("Nikolche Mihajlovski")
@Since("5.0.11")
@ExpectErrors
public class HttpErrorHandlerTest extends HttpTestCommons {

	@Test
	public void testErrorHandler1() {
		On.custom().errorHandler(new ErrorHandler() {
			@Override
			public Object handleError(Req req, Resp resp, Throwable e) throws Exception {
				if (e instanceof NotFound) return Defaults.errorHandler().handleError(req, resp, e); // default error processing
				return req + ":err:" + e;
			}
		});

		On.get("/err").html(new ReqHandler() {
			@SuppressWarnings("null")
			@Override
			public Object execute(Req req) throws Exception {
				String s = null;
				return s.toString(); // NPE
			}
		});

		onlyGet("/err?x=1");
	}

	@Test
	public void testErrorHandler2() {
		On.custom().errorHandler(new ErrorHandler() {
			@Override
			public Object handleError(Req req, Resp resp, Throwable e) throws Exception {
				if (e instanceof NotFound) return Defaults.errorHandler().handleError(req, resp, e); // default error processing
				return resp.code(200).result(req + ":err2:" + e);
			}
		});

		On.get("/err2").json(new ReqHandler() {
			@SuppressWarnings("null")
			@Override
			public Object execute(Req req) throws Exception {
				String s = null;
				return s.toString(); // NPE
			}
		});

		onlyGet("/err2?x=2");
	}

}
