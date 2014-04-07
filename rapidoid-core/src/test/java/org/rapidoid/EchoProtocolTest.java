package org.rapidoid;

/*
 * #%L
 * rapidoid-core
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import org.rapidoid.Ctx;
import org.rapidoid.Protocol;
import org.rapidoid.util.U;
import org.testng.annotations.Test;

public class EchoProtocolTest extends RapidoidTestCommons {

	@Test
	public void echo() {

		server(new Protocol() {

			@Override
			public void process(Ctx ctx) {
				ctx.write(ctx.readln().toUpperCase());
				ctx.write("\n");
				ctx.complete(false);
			}

		});

		// FIXME use client instead of manual testing
		U.sleep(10000000);
	}

	@Test
	public void echoAsync() {
		server(new Protocol() {

			@Override
			public void process(final Ctx ctx) {
				final String s = ctx.readln();
				U.schedule(new Runnable() {

					@Override
					public void run() {
						ctx.write(s.toUpperCase() + "\n");
						ctx.complete(false);
					}

				}, 2000);
			}

		});

		// FIXME use client instead of manual testing
		U.sleep(10000000);
	}

}
