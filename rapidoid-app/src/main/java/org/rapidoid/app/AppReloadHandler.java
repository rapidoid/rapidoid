package org.rapidoid.app;

/*
 * #%L
 * rapidoid-app
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import java.io.File;
import java.io.FileInputStream;

import org.rapidoid.http.Handler;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.util.CustomizableClassLoader;
import org.rapidoid.util.IO;
import org.rapidoid.util.U;

public class AppReloadHandler implements Handler, Mapper<String, byte[]> {

	private final String path;

	public AppReloadHandler(String path) {
		this.path = properPath(path);
	}

	private static String properPath(String path) {
		if (!path.endsWith(File.separator)) {
			path += File.separator;
		}

		File dir = new File(path);

		U.must(dir.exists() && dir.isDirectory(), "The folder doesn't exist: %s", path);

		return path;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object handle(HttpExchange x) throws Exception {

		CustomizableClassLoader classLoader = new CustomizableClassLoader(this,
				(Predicate<String>) Predicate.ALWAYS_TRUE, true);

		AppHandler handler = new AppHandler(classLoader);
		return handler.handle(x);
	}

	@Override
	public byte[] map(String classname) throws Exception {
		String filename = path + classname.replace('.', '/') + ".class";

		File file = new File(filename);
		if (file.exists()) {
			return IO.loadBytes(new FileInputStream(file));
		} else {
			return null;
		}
	}

}
