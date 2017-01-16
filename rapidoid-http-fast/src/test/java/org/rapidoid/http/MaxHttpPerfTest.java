package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http-fast
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.buffer.BufGroup;
import org.rapidoid.bytes.BytesUtil;
import org.rapidoid.env.Env;
import org.rapidoid.data.BufRanges;
import org.rapidoid.http.impl.HttpParser;
import org.rapidoid.net.Protocol;
import org.rapidoid.net.TCP;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.impl.RapidoidHelper;
import org.rapidoid.util.Msc;
import org.rapidoid.wrap.IntWrap;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class MaxHttpPerfTest {

	protected static final byte[] PREFIX = "Connection:".getBytes();

	protected static final byte[] RESP = "HTTP/1.1 200 OK\r\nConnection: keep-alive\r\nContent-Length: 1\r\n\r\nX"
		.getBytes();

	public static void main(String[] args) {
		Env.setArgs(args);

		String req = "GET /plaintext HTTP/1.1\r\nHost:www.test.com\r\n\r\n";

		BufGroup gr = new BufGroup(16 * 1024);
		final Buf buf = gr.newBuf();
		buf.append(req);

		final RapidoidHelper helper = new RapidoidHelper();

		final HttpParser parser = new HttpParser();

		for (int i = 0; i < 10; i++) {
			Msc.benchmark("HTTP parse", 3000000, new Runnable() {
				public void run() {
					buf.position(0);

					parser.parse(buf, helper);
				}
			});
		}

		TCP.server().protocol(new Protocol() {
			@Override
			public void process(Channel ctx) {
				if (ctx.isInitial()) {
					return;
				}

				BufRanges lines = ctx.helper().ranges1;
				lines.count = 0;

				Buf in = ctx.input();

				IntWrap pos = ctx.helper().integers[0];

				int poss = BytesUtil.parseLines(in.bytes(), lines, pos, in.position(), in.size(), (byte) 'v',
					(byte) 'e');

				in.position(poss);

				ctx.write(RESP);
			}
		}).build().start();
	}

}
