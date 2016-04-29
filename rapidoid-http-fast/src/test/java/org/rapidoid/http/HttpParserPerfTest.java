package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http-fast
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.buffer.BufGroup;
import org.rapidoid.data.BufRange;
import org.rapidoid.data.BufRanges;
import org.rapidoid.http.impl.HttpParser;
import org.rapidoid.net.impl.RapidoidHelper;
import org.rapidoid.util.Msc;
import org.rapidoid.wrap.BoolWrap;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class HttpParserPerfTest {

	private static final BufGroup BUFS = new BufGroup(10);

	static String REQ1 = "GET /asd/ff?a=5&bn=4 HTTP/1.1|Host:www.test.com|Set-Cookie: a=2|Connection: keep-alive|Set-Cookie: aaa=2|Set-Cookie: aaa=2|Set-Cookie: aaa=2||";
	static String REQ2 = "POST /opa/dd/fggh HTTP|Host:a.b.org|My-Header: ghhh|Content-Length: 1|My-Header: ghhh|Connection: keep-alive|My-Header: ghhh|My-Header: ghhh|My-Header: ghhh||X";
	static String REQ3 = "DELETE /ff?ba=fg F|AAAAA: aaa=2|AAAAA: aaa=2|AAAAA: aaa=2|Content-Length:0|AAAAA: aaa=2|AAAAA: aaa=2|Connection: keep-alive|AAAAA: aaa=2||";
	static String REQ4 = "PUT /books MY-PROTOCOL|Conf:|Set-Cookie: aaa=2|Set-Cookie: aaa=2|Content-Length:10|Set-Cookie: aaa=2|Set-Cookie: aaa=2|Set-Cookie: aaa=2||abcdefghij";

	protected static final BufRanges helpers = new BufRanges(100);

	public static void main(String[] args) {

		final HttpParser parser = new HttpParser();
		final Buf[] reqs = {r(REQ1), r(REQ2), r(REQ3), r(REQ4)};
		final RapidoidHelper helper = new RapidoidHelper(null);

		BufRange[] ranges = helper.ranges1.ranges;
		final BufRanges headers = helper.ranges2;

		final BoolWrap isGet = helper.booleans[0];
		final BoolWrap isKeepAlive = helper.booleans[1];

		final BufRange verb = ranges[ranges.length - 1];
		final BufRange uri = ranges[ranges.length - 2];
		final BufRange path = ranges[ranges.length - 3];
		final BufRange query = ranges[ranges.length - 4];
		final BufRange protocol = ranges[ranges.length - 5];
		final BufRange body = ranges[ranges.length - 6];

		for (int i = 0; i < 10; i++) {
			Msc.benchmark("parse", 3000000, new Runnable() {
				int n;

				@Override
				public void run() {
					Buf buf = reqs[n % 4];
					buf.position(0);
					parser.parse(buf, isGet, isKeepAlive, body, verb, uri, path, query, protocol, headers, helper);
					n++;
				}
			});
		}

		System.out.println(BUFS.instances() + " buffer instances.");
	}

	private static Buf r(String req) {
		req = req.replaceAll("\\|", "\r\n");
		System.out.println("Request size: " + req.length());
		return BUFS.from(req, "");
	}

}
