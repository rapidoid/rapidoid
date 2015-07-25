package org.rapidoid.io.watch;

/*
 * #%L
 * rapidoid-watch
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class ClassReloader extends ClassLoader {

	private final List<String> names;
	private final ClassLoader parent;
	private final String dir;
	private final long createdOn = System.currentTimeMillis();

	public ClassReloader(String dir, ClassLoader parent, List<String> names) {
		super(parent);
		this.dir = dir;
		this.parent = parent;
		this.names = names;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {

		String filename = null;
		File ff = new File(dir, getClassRelativePath(name));

		if (ff.exists()) {
			filename = ff.getAbsolutePath();
		} else {
			URL res = parent.getResource(getClassRelativePath(name));
			if (res != null) {
				try {
					filename = res.toURI().getPath();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		}

		if (filename != null) {
			try {
				Log.info("Hot swap", "file", filename);

				try {
					try {
						Thread.sleep(300);
					} catch (Exception e2) {}

					byte[] classData = readFile(new File(filename));
					return defineClass(name, classData, 0, classData.length);

				} catch (ClassFormatError e) {
					// try again 1 second later
					try {
						Thread.sleep(1000);
					} catch (Exception e2) {}

					byte[] classData = readFile(new File(filename));
					return defineClass(name, classData, 0, classData.length);
				}

			} catch (Exception e) {
				throw new ClassNotFoundException("Couldn't find class: " + name);
			}
		} else {
			return super.findClass(name);
		}
	}

	private byte[] readFile(File file) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		BufferedInputStream stream = null;
		try {
			stream = new BufferedInputStream(new FileInputStream(file));

			byte[] bytes = new byte[16 * 1024];

			int read = -1;
			while ((read = stream.read(bytes)) != -1) {
				output.write(bytes, 0, read);
			}

			return output.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	public Class<?> loadClass(String classname) throws ClassNotFoundException {
		Log.info("Loading class", "name", classname);

		if (inDir(classname) || names.contains(classname)) {
			try {
				return findClass(classname);
			} catch (ClassNotFoundException e) {
				Class<?> fallbackClass = super.loadClass(classname);
				Log.info("Couldn't reload class, fallback load", "name", classname);
				return fallbackClass;
			}
		} else {
			return super.loadClass(classname);
		}
	}

	private boolean inDir(String classname) {
		return new File(dir, getClassRelativePath(classname)).exists();
	}

	private static String getClassRelativePath(String classname) {
		return classname.replace('.', File.separatorChar) + ".class";
	}

	public void add(List<String> classnames) {
		names.addAll(classnames);
	}

	public long getCreatedOn() {
		return createdOn;
	}

}
