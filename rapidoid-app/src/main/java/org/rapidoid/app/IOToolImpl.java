package org.rapidoid.app;

/*
 * #%L
 * rapidoid-app
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

import java.io.File;
import java.util.List;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.io.IO;
import org.rapidoid.io.Res;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("4.2.0")
public class IOToolImpl implements IOTool {

	@Override
	public List<File> files(String dir) {
		List<String> names = filenames(dir);
		List<File> files = U.list();

		for (String name : names) {
			files.add(new File(name));
		}

		return files;
	}

	@Override
	public List<String> filenames(String dir) {
		List<String> found = U.list();
		IO.findAll(new File(dir), found);
		return found;
	}

	@Override
	public byte[] load(String filename) {
		return Res.from(filename).getBytes();
	}

	@Override
	public void save(String filename, byte[] data) {
		IO.save(filename, data);
	}

	@Override
	public File file(String filename) {
		return IO.file(filename);
	}

}
