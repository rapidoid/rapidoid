package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.Btn;
import org.rapidoid.gui.GUI;
import org.rapidoid.gui.reqinfo.IReqInfo;
import org.rapidoid.gui.reqinfo.ReqInfo;
import org.rapidoid.html.Tag;
import org.rapidoid.http.HttpVerb;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.u.U;
import org.rapidoid.util.Tokens;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
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
public class DeploymentHandler extends GUI implements Callable<Object> {

	@Override
	public Object call() throws Exception {
		List<Object> info = U.list();

		if (ClasspathUtil.hasAppJar()) {

			IReqInfo req = ReqInfo.get();

			String appJar = ClasspathUtil.appJar();
			String stagedAppJar = appJar + ".staged";

			info.add(h3("Deployment status:"));
			info.add(grid(jarsInfo(appJar, stagedAppJar)));

			info.add(h3("Upload an application JAR to stage it and then deploy it:"));
			info.add(hardcoded("<form action=\"/_stage\" class=\"dropzone\" id=\"jar-upload\"></form>"));

			String token = Tokens.serialize(U.<String, Serializable>map("username", req.username()));

			info.add(h3("HTTP API for Deployment:"));

			info.add(grid(apisInfo()));

			info.add(h3("Packaging and deploying with Maven:"));
			String cmd = "mvn clean org.rapidoid:deploy:uber-jar";

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

	@SuppressWarnings("unchecked")
	private List<Map<String, ?>> jarsInfo(String appJar, String stagedAppJar) {
		return U.list(
			jarInfo(appJar, "Currently deployed application JAR"),
			jarInfo(stagedAppJar, "Application JAR to be deployed")
		);
	}

	private Map<String, ?> jarInfo(String filename, String desc) {
		File file = new File(filename);

		String size = file.exists() ? (file.length() / 1024) + " KB" : "";

		return U.map("file", filename, "description", desc, "exists", display(file.exists()), "size", size);
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, ?>> apisInfo() {
		return U.list(
			apiInfo("Staging of the application JAR",
				verb(HttpVerb.POST), "/_stage",
				U.map("_token", "Deployment token", "file", "<your-jar>"),
				"curl -F 'file=@app.jar' 'http://example.com/_stage?_token=4F920B"),

			apiInfo("Deployment of the staged JAR",
				verb(HttpVerb.POST), "/_deploy",
				U.map("_token", "Deployment token"),
				"curl -X POST 'http://example.com/_deploy?_token=4F920B")
		);
	}

	private Map<String, ?> apiInfo(String desc, Tag verb, String uri, Map<String, String> params, String example) {
		return U.map("description", desc, "verb", verb, "uri", span(uri).class_("text-box"), "parameters", grid(params).headless(true), "example", example);
	}

}
