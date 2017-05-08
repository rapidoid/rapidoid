package org.rapidoid.docs.httpasync;

import org.rapidoid.http.Req;
import org.rapidoid.job.Jobs;
import org.rapidoid.setup.On;

import java.util.concurrent.TimeUnit;

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
		/* Wait 1 second before returning a response */

		On.get("/").json((Req req) -> Jobs.schedule(() -> {

			req.response().result("OK").done();

		}, 1, TimeUnit.SECONDS));
	}

}
