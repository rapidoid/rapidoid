package org.rapidoidx;

/*
 * #%L
 * rapidoid-x-net
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.F2;
import org.rapidoid.util.UTILS;
import org.rapidoidx.net.Protocol;
import org.rapidoidx.net.abstracts.Channel;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
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
				UTILS.connect("localhost", 8080, new F2<Void, BufferedReader, DataOutputStream>() {
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

				UTILS.schedule(new Runnable() {
					@Override
					public void run() {
						ctx.write(in.toUpperCase()).write(CR_LF).done().closeIf(in.equals("bye"));
					}
				}, 1000);

				ctx.async();
			}

		}, new Runnable() {
			@Override
			public void run() {
				UTILS.connect("localhost", 8080, new F2<Void, BufferedReader, DataOutputStream>() {
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
