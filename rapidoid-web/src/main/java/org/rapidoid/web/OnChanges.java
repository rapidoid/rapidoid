package org.rapidoid.web;

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
import org.rapidoid.config.Conf;
import org.rapidoid.http.FastHttp;
import org.rapidoid.log.Log;
import org.rapidoid.scan.ClasspathUtil;

import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class OnChanges {

	private final ServerSetup serverSetup;
	private final FastHttp[] fastHttps;

	public OnChanges(ServerSetup serverSetup, FastHttp[] fastHttps) {
		this.serverSetup = serverSetup;
		this.fastHttps = fastHttps;
	}

	public void reload() {
		if (Conf.dev()) {
			Set<String> classpathFolders = ClasspathUtil.getClasspathFolders();
			Log.info("Watching classpath for changes...", "classpath", classpathFolders);
			// FIXME complete this
		}
	}

}
