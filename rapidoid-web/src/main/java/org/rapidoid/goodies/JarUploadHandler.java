package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.io.IO;
import org.rapidoid.io.Upload;
import org.rapidoid.log.Log;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.util.AnsiColor;

/*
 * #%L
 * rapidoid-web
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class JarUploadHandler extends GUI implements ReqHandler {

	@Override
	public Object execute(Req req) throws Exception {
		String appJar = ClasspathUtil.appJar();

		if (appJar != null) {
			Upload jar = req.file("file");
			IO.save(appJar, jar.content());
			Log.info("Saved new JAR", "size", jar.content().length, "destination", appJar);

			if (req.header("User-Agent", "").toLowerCase().startsWith("curl/")) {
				return req.response().plain(AnsiColor.green("Successfully uploaded the JAR.\n"));
			} else {
				return "OK";
			}

		} else {
			return "Not possible!";
		}
	}

}
