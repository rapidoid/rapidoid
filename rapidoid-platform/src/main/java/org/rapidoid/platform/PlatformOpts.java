package org.rapidoid.platform;

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
import org.rapidoid.env.Env;
import org.rapidoid.util.LazyInit;
import org.rapidoid.util.Msc;

import java.io.File;

@Authors("Nikolche Mihajlovski")
@Since("5.4.7")
public class PlatformOpts extends RapidoidThing {

	private static volatile String singleAppPath = "/app";
	private static volatile String appsPath = "/apps";
	private static volatile String platformPath = "/platform";

	private static final LazyInit<Boolean> singleApp = new LazyInit<>(PlatformOpts::hasAppFolder);

	public static void reset() {
		singleAppPath = "/app";
		appsPath = "/apps";
		platformPath = "/platform";
		singleApp.reset();
	}

	public static boolean hasAppFolder() {
		File app = new File(singleAppPath);
		return app.exists() && app.isDirectory();
	}

	public static boolean isMultiApp() {
		return Msc.isPlatform() && !isSingleApp();
	}

	public static boolean isMultiProcess() {
		return Msc.isPlatform() && (!isSingleApp() || Env.dev());
	}

	public static boolean isSingleApp() {
		return singleApp.get();
	}

	public static void singleApp(boolean singleApp) {
		PlatformOpts.singleApp.setValue(singleApp);
	}

	public static String singleAppPath() {
		return singleAppPath;
	}

	public static void singleAppPath(String singleAppPath) {
		PlatformOpts.singleAppPath = singleAppPath;
	}

	public static String appsPath() {
		return appsPath;
	}

	public static void appsPath(String appsPath) {
		PlatformOpts.appsPath = appsPath;
	}

	public static String platformPath() {
		return platformPath;
	}

	public static void platformPath(String platformPath) {
		PlatformOpts.platformPath = platformPath;
	}

}
