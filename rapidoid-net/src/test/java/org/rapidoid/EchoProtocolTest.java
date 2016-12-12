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
import org.rapidoid.io.IO;
import org.rapidoid.lambda.F3;
import org.rapidoid.net.Protocol;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class EchoProtocolTest extends NetTestCommons {

	private static final String[] testCases = {
		"abc\nxy\nbye\n",
		"abc\r\nxy\r\nbye\r\n",
		"abc\nbye\n",
		"abc\r\nbye\r\n",
	};

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
				connectAndExercise();
			}
		});
	}

	private void connectAndExercise() {
		Msc.connect("localhost", 8888, new F3<Void, InputStream, BufferedReader, DataOutputStream>() {
			@Override
			public Void execute(InputStream inputStream, BufferedReader in, DataOutputStream out) throws IOException {
				out.writeBytes("hello\n");
				eq(in.readLine(), "HELLO");

				out.writeBytes("Foo\n");
				eq(in.readLine(), "FOO");

				out.writeBytes("bye\n");
				eq(in.readLine(), "BYE");

				return null;
			}
		});

		for (final String testCase : testCases) {
			Msc.connect("localhost", 8888, new F3<Void, InputStream, BufferedReader, DataOutputStream>() {
				@Override
				public Void execute(InputStream inputStream, BufferedReader in, DataOutputStream out) throws IOException {
					out.writeBytes(testCase);

					List<String> lines = IO.readLines(in);
					List<String> expected = U.list(testCase.toUpperCase().split("\r?\n"));

					eq(lines, expected);
					return null;
				}
			});
		}
	}

	@Test
	public void echoAsync() {

		final AtomicInteger started = new AtomicInteger();
		final AtomicInteger finished = new AtomicInteger();

		server(new Protocol() {

			@Override
			public void process(final Channel ctx) {
				final String in = ctx.readln();
				final int order = started.incrementAndGet();

				Msc.EXECUTOR.schedule(new Runnable() {
					@Override
					public void run() {

						while (order > finished.get() + 1) {
							U.sleep(10);
						}

						ctx.write(in.toUpperCase()).write(CR_LF).send();

						if (in.equals("bye")) {
							ctx.done();
							ctx.close();
						}

						finished.incrementAndGet();
					}
				}, 1, TimeUnit.SECONDS);

				ctx.async();
			}

		}, new Runnable() {
			@Override
			public void run() {
				connectAndExercise();
			}
		});
	}

}
