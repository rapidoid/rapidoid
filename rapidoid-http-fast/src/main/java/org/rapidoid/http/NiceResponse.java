package org.rapidoid.http;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;
import org.rapidoid.util.AnsiColor;
import org.rapidoid.util.Msc;

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

@Authors("Nikolche Mihajlovski")
@Since("5.2.4")
public class NiceResponse extends RapidoidThing {

	public static Object ok(Req req, String msg) {

		if (isCurl(req)) {
			return req.response().plain(AnsiColor.green(msg + "\n"));
		}

		return details(msg, true);
	}

	public static Object err(Req req, String msg) {

		if (isCurl(req)) {
			return req.response().plain(AnsiColor.red(msg + "\n"));
		}

		return details(msg, false);
	}

	public static Object err(Req req, Throwable err) {
		return err(req, Msc.errorMsg(err));
	}

	public static Object deny(Req req) {
		return err(req, "Access denied!");
	}

	public static Object details(String msg, boolean success) {
		return U.map("msg", msg, "success", success);
	}

	private static boolean isCurl(Req req) {
		return req.header("User-Agent", "").toLowerCase().startsWith("curl/");
	}

}
