package org.rapidoid;

/*
 * #%L
 * rapidoid-net
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
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.F2;
import org.rapidoid.net.Protocol;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.util.Msc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class EchoProtocolTest extends NetTestCommons {

	@Test
	public void echo() {
		server(new Protocol() {

			@Override
			public void process(Channel ctx) {
				String in = ctx.readln();
				ctx.write(in.toUpperCase()).write(CR_LF).closeIf(in.equals("bye"));
			}

		}, new Runnable() {
			@Override
			public void run() {
				Msc.connect("localhost", 8888, new F2<Void, BufferedReader, DataOutputStream>() {
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
		server(new Protocol() {

			@Override
			public void process(final Channel ctx) {
				final String in = ctx.readln();

				Msc.EXECUTOR.schedule(new Runnable() {
					@Override
					public void run() {
						ctx.write(in.toUpperCase()).write(CR_LF).done().closeIf(in.equals("bye"));
					}
				}, 1, TimeUnit.SECONDS);

				ctx.async();
			}

		}, new Runnable() {
			@Override
			public void run() {
				Msc.connect("localhost", 8888, new F2<Void, BufferedReader, DataOutputStream>() {
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
