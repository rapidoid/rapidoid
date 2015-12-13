package org.rapidoid.http;

/*
 * #%L
 * rapidoid-integration-tests
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Rnd;
import org.rapidoid.http.fast.On;
import org.rapidoid.http.fast.ReqHandler;
import org.rapidoid.io.IO;
import org.rapidoid.job.Jobs;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class HttpServerHeadersTest extends HttpTestCommons {

	private static final int N = 100;

	@Test
	public void shouldHandleVariousHttpRequests() throws IOException, URISyntaxException {
		On.get("/fileabc").html(new ReqHandler() {
			@Override
			public Object handle(Req x) {
				return x.response().filename("abc.txt").content("abcde");
			}
		});

		On.get("/bin").binary(new ReqHandler() {
			@Override
			public Object handle(Req x) {
				return "bin";
			}
		});

		On.get("/session").html(new ReqHandler() {
			@Override
			public Object handle(Req x) {
				if (x.cookie("ses", null) == null) {
					x.cookies().put("ses", "023B");
				}
				x.cookies().put("key" + Rnd.rnd(100), "val" + Rnd.rnd(100));

				return x.response().html("oki");
			}
		});

		On.get("/async").html(new ReqHandler() {
			@Override
			public Object handle(final Req x) {
				x.async();
				Jobs.schedule(new Runnable() {
					@Override
					public void run() {
						x.response().content("now").done();
					}
				}, 50, TimeUnit.MILLISECONDS);
				return x;
			}
		});

		On.get("/testfile1").html(new ReqHandler() {
			@Override
			public Object handle(Req x) {
				return IO.file("test1.txt");
			}
		});

		On.get("/rabbit.jpg").html(new ReqHandler() {
			@Override
			public Object handle(Req x) {
				return x.response().file(IO.file("rabbit.jpg"));
			}
		});

		On.get("/ab").html(new ReqHandler() {
			@Override
			public Object handle(Req x) {
				return x.response().file(IO.file("ab.html"));
			}
		});

		On.req(new ReqHandler() {
			@Override
			public Object handle(Req x) {
				x.cookies().put("asd", "f");
				return x.response().html("a<b>b</b>c");
			}
		});

		byte[] ab = FileUtils.readFileToByteArray(IO.file("ab.html"));

		for (int i = 0; i < N; i++) {
			eq(get("/"), "a<b>b</b>c");
			eq(get("/xy"), "a<b>b</b>c");
			eq(get("/async"), "now");
			eq(get("/session"), "oki");
			eq(get("/bin"), "bin");
			eq(get("/fileabc"), "abcde");
			eq(get("/testfile1"), "TEST1");
			eq(getBytes("/ab"), ab);
		}
	}

	@Test
	public void shouldRenderRabbit() throws Exception { // :)
		On.get("/rabbit.jpg").html(new ReqHandler() {
			@Override
			public Object handle(Req x) {
				return x.response().file(IO.file("rabbit.jpg"));
			}
		});

		byte[] rabbit = FileUtils.readFileToByteArray(IO.file("rabbit.jpg"));

		for (int i = 0; i < N; i++) {
			eq(getBytes("/rabbit.jpg"), rabbit);
		}
	}

}
