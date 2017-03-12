package org.rapidoid.process;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.io.File;

/*
 * #%L
 * rapidoid-commons
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
@Since("5.3.0")
public class Proc extends RapidoidThing {

	public static ProcessHandle run(String... command) {
		return new ProcessParams().run(command);
	}

	public static ProcessParams in(File dir) {
		return new ProcessParams().in(dir);
	}

	public static ProcessParams in(String dir) {
		return in(new File(dir));
	}

	public static ProcessParams group(Processes group) {
		return new ProcessParams().group(group);
	}

	public static ProcessParams id(String id) {
		return new ProcessParams().id(id);
	}

	public static ProcessParams printingOutput(boolean printingOutput) {
		return new ProcessParams().printingOutput(printingOutput);
	}

	public static ProcessParams linePrefix(String linePrefix) {
		return new ProcessParams().linePrefix(linePrefix);
	}

}
