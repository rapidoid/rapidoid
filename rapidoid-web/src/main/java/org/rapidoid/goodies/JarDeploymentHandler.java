package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;
import org.rapidoid.http.NiceResponse;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.log.Log;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.u.U;

import java.io.File;

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
public class JarDeploymentHandler extends GUI implements ReqHandler {

	private static final String SUCCESS = "Successfully deployed the jar, restarting...";

	private static final String NOT_POSSIBLE = "Not possible!";

	@Override
	public Object execute(Req req) throws Exception {
		String appJar = ClasspathUtil.appJar();
		String stagedAppJar = appJar + ".staged";

		if (U.notEmpty(appJar)) {

			new File(appJar).delete();
			new File(stagedAppJar).renameTo(new File(appJar));

			Log.info("Successfully deployed the staged jar", "staged", stagedAppJar, "deployed", appJar);

			return NiceResponse.ok(req, SUCCESS);

		} else {
			return NiceResponse.err(req, NOT_POSSIBLE);
		}
	}

}
