package org.rapidoid.deploy;

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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HTTP;
import org.rapidoid.http.HttpClient;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.io.ByteArrayInputStream;
import java.io.File;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class AppDownloader extends RapidoidThing {

	private static final String REPOSITORY_NAME = "[-\\w]+";
	private static final String GITHUB_REPO_ZIP = "https://github.com/%s/%s/archive/master.zip";

	private static final HttpClient client = HTTP.client().followRedirects(true);

	public static void download(String appRef, String appsFolder) {
		String url = getAppUrl(appRef);

		Log.info("Downloading application", "application", appRef, "url", url);
		byte[] zip = client.get(url).execute().bodyBytes();

		String zipRoot = Msc.detectZipRoot(new ByteArrayInputStream(zip));

		String destination, zipDest;

		if (zipRoot != null) {
			destination = appsFolder + File.separator + zipRoot;
			zipDest = appsFolder;

		} else {
			destination = appsFolder + File.separator + Msc.textToId(appRef);
			zipDest = destination;
		}

		Log.info("Extracting application", "application", appRef, "destination", destination);
		Msc.unzip(new ByteArrayInputStream(zip), zipDest);
	}

	public static String getAppUrl(String appRef) {
		String url;

		if (appRef.matches(REPOSITORY_NAME)) {
			url = U.frmt(GITHUB_REPO_ZIP, "rapidoid", appRef);

		} else if (appRef.matches(REPOSITORY_NAME + "/" + REPOSITORY_NAME)) {
			url = U.frmt(GITHUB_REPO_ZIP, (Object[]) appRef.split("/"));

		} else {
			url = appRef;
		}
		return url;
	}

}
