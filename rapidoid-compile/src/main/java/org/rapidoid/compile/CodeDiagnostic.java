package org.rapidoid.compile;

import org.rapidoid.annotation.Authors;

/*
 * #%L
 * rapidoid-compile
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

@Authors("Nikolche Mihajlovski")
public class CodeDiagnostic {

	public String message;

	public String filename;

	public int line;

	public int start;

	public int end;

	@Override
	public String toString() {
		return "CodeDiagnostic [message=" + message + ", filename=" + filename + ", line=" + line + ", start=" + start
				+ ", end=" + end + "]";
	}

}
