package org.rapidoid.compile;

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

import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.compile.impl.EcjCompilationUnit;
import org.rapidoid.compile.impl.EcjCompiler;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class Compile {

	private static EcjCompiler getCompiler() {
		return new EcjCompiler();
	}

	public static JavaSource source(String packageName, String name, String sourceCode) {
		return new JavaSource(packageName, name, sourceCode);
	}

	public static Compilation compile(String... sources) {
		ICompilationUnit[] units = new ICompilationUnit[sources.length];

		for (int i = 0; i < units.length; i++) {
			units[i] = new EcjCompilationUnit(sources[i]);
		}

		return getCompiler().compile(units);
	}

	public static Compilation compile(JavaSource... sources) {
		ICompilationUnit[] units = new ICompilationUnit[sources.length];

		for (int i = 0; i < units.length; i++) {
			units[i] = new EcjCompilationUnit(sources[i].packageName, sources[i].name, sources[i].source);
		}

		return getCompiler().compile(units);
	}

}
