package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.Btn;
import org.rapidoid.gui.GUI;
import org.rapidoid.gui.reqinfo.ReqInfo;
import org.rapidoid.http.HttpVerb;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.u.U;

import java.util.List;
import java.util.concurrent.Callable;

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
public class DeployHandler extends GUI implements Callable<Object> {

	@Override
	public Object call() throws Exception {
		List<Object> info = U.list();

		if (ClasspathUtil.hasAppJar()) {

			info.add(h2("Upload an application JAR to re-deploy:"));
			info.add(hardcoded("<form action=\"/_/jar\" class=\"dropzone\" id=\"jar-upload\"></form>"));

			String token = U.or(ReqInfo.get().cookies().get("_token"), "");

			info.add(h2("HTTP API for Deployment:"));
			info.add(h6(verb(HttpVerb.POST), b(" http://your-app-domain/_/jar?_token=<token>")));
			info.add(h6(b("POST DATA: file=<your-jar>")));

			info.add(h2("Building and deploying with Maven:"));
			String cmd = "mvn clean package && cp target/*.jar target/_app_.jar && curl -F 'file=@target/_app_.jar' 'http://localhost:8888/_/jar?_token=" + token + "'";

			info.add(h6(copy(b(cmd))));

			Btn shutdown = btn("Shutdown / Restart").danger()
				.confirm("Do you really want to SHUTDOWN / RESTART the application?")
				.onClick(new Runnable() {
					@Override
					public void run() {
						TerminateHandler.shutdownSoon();
					}
				});

			info.add(br());
			info.add(shutdown);

		} else {
			info.add(h3(WARN, " No ", b("app.jar"), " file was configured on the classpath, so application deployment is disabled!"));
			info.add(h4("Application deployment works by uploading a JAR which overwrites the file 'app.jar', and restarting the application."));
		}

		return multi(info);
	}

}
