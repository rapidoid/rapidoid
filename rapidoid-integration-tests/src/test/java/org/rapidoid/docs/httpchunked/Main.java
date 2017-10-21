package org.rapidoid.docs.httpchunked;

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

import org.rapidoid.job.Jobs;
import org.rapidoid.setup.App;
import org.rapidoid.setup.On;

public class Main {

	public static void main(String[] args) {
		App.run(args);

		On.get("/hello").plain((req, resp) -> {

			req.async(); // mark asynchronous request processing

			// send part 1
			resp.chunk("part 1".getBytes());

			// after some time, send part 2 and finish
			Jobs.after(100).milliseconds(() -> {
				resp.chunk(" & part 2".getBytes());
				resp.done();
			});

			return resp;
		});
	}

}
