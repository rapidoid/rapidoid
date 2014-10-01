package org.rapidoid;

/*
 * #%L
 * rapidoid-net
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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.impl.Protocol;
import org.rapidoid.util.F2;
import org.rapidoid.util.U;
import org.testng.annotations.Test;

public class EchoProtocolTest extends NetTestCommons {

	@Test
	public void echo() {
		
		U.setLogLevel(U.DEBUG);
		
		server(new Protocol() {

			@Override
			public void process(Channel ctx) {
				String in = ctx.readln();
				boolean stop = in.equals("bye");

				ctx.write(in.toUpperCase());
				ctx.write("\n");
				ctx.done();

				if (stop) {
					ctx.close();
				}
			}

		}, new Runnable() {
			@Override
			public void run() {
				U.connect("localhost", 8080, new F2<Void, BufferedReader, DataOutputStream>() {
					@Override
					public Void execute(BufferedReader in, DataOutputStream out) throws IOException {
						out.writeBytes("hello\n");
						eq(in.readLine(), "HELLO");

						out.writeBytes("Foo\n");
						eq(in.readLine(), "FOO");

						out.writeBytes("bye\n");
						eq(in.readLine(), "BYE");

						return null;
					}
				});
			}
		});
	}

	@Test
	public void echoAsync() {
		
		U.setLogLevel(U.DEBUG);
		
		server(new Protocol() {

			@Override
			public void process(final Channel ctx) {
				final String in = ctx.readln();
				final boolean stop = in.equals("bye");

				U.schedule(new Runnable() {

					@Override
					public void run() {
						ctx.write(in.toUpperCase() + "\n");
						ctx.done();

						if (stop) {
							ctx.close();
						}
					}

				}, 2000);
			}

		}, new Runnable() {
			@Override
			public void run() {
				U.connect("localhost", 8080, new F2<Void, BufferedReader, DataOutputStream>() {
					@Override
					public Void execute(BufferedReader in, DataOutputStream out) throws IOException {
						out.writeBytes("a\n");
						eq(in.readLine(), "A");

						out.writeBytes("bb\n");
						eq(in.readLine(), "BB");

						out.writeBytes("bye\n");
						eq(in.readLine(), "BYE");

						return null;
					}
				});
			}
		});
	}

}
