package com.rapidoid.http;

/*
 * #%L
 * rapidoid-http
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

import org.rapidoid.buffer.Buf;
import org.rapidoid.buffer.BufGroup;
import org.rapidoid.util.U;

public class HttpParserPerfTest {

	private static final BufGroup BUFS = new BufGroup(10);

	static String REQ1 = "GET /asd/ff?a=5&bn=4 HTTP/1.1|Host:www.test.com|Set-Cookie: aaa=2|Set-Cookie: aaa=2|Set-Cookie: aaa=2|Set-Cookie: aaa=2|Set-Cookie: aaa=2|";
	static String REQ2 = "POST /opa/dd/fggh HTTP|Host:a.b.org|My-Header: ghhh|My-Header: ghhh|My-Header: ghhh|My-Header: ghhh|My-Header: ghhh|My-Header: ghhh|";
	static String REQ3 = "DELETE /ff?ba=fg F|AAAAA: aaa=2|AAAAA: aaa=2|AAAAA: aaa=2|AAAAA: aaa=2|AAAAA: aaa=2|AAAAA: aaa=2|AAAAA: aaa=2|";
	static String REQ4 = "PUT /books MY-PROTOCOL|AAAAA: aaa=2|Set-Cookie: aaa=2|Set-Cookie: aaa=2|Set-Cookie: aaa=2|Set-Cookie: aaa=2|Set-Cookie: aaa=2|Set-Cookie: aaa=2|";

	static String EXTRA = "aaaaaaaaa : bbbbbbbbbb|cccccccccccc:ddddddddddd|eeeeeeeeeeeeeee:fffffffffffffff|";

	public static void main(String[] args) {
		final HttpParser parser = new HttpParser();
		final Buf[] reqs = { r(REQ1), r(REQ2), r(REQ3), r(REQ4) };

		final WebExchangeImpl req = new WebExchangeImpl();

		U.benchmark("parse", 1000000, new Runnable() {
			int n;

			@Override
			public void run() {
				req.reset();
				Buf buf = reqs[n % 4];
				buf.position(0);
				parser.parse(buf, req);
				n++;
			}
		});

		U.print(BUFS.instances() + " buffer instances.");
	}

	private static Buf r(String req) {
		req += EXTRA + EXTRA + EXTRA;
		req += "|";
		req = req.replaceAll("\\|", "\r\n");
		U.print("Request size: " + req.length());
		return BUFS.from(req, "");
	}

}
