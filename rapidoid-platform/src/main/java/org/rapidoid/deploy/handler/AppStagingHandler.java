package org.rapidoid.deploy.handler;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.deploy.AppDeployment;
import org.rapidoid.deploy.Apps;
import org.rapidoid.deploy.SingleApp;
import org.rapidoid.gui.GUI;
import org.rapidoid.http.NiceResponse;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.io.Upload;
import org.rapidoid.log.Log;
import org.rapidoid.util.Msc;

/*
 * #%L
 * rapidoid-platform
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
@Since("5.1.0")
public class AppStagingHandler extends GUI implements ReqHandler {

	@Override
	public Object execute(Req req) throws Exception {
		try {
			Upload upload = req.file("file");

			AppDeployment app;
			if (Msc.isSingleApp()) {
				app = SingleApp.get();
			} else {
				app = AppDeployment.fromFilename(upload.filename());
			}

			app.stage(upload.filename(), upload.content());

		} catch (Exception e) {
			Log.error("Staging failed!", e);
			return NiceResponse.err(req, e);

		} finally {
			Apps.reload();
		}

		return NiceResponse.ok(req, "Successfully staged the application");
	}

}
