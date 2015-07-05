package org.rapidoidx.compile;

/*
 * #%L
 * rapidoid-x-compile
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoidx.compile.impl.EcjCompilationUnit;
import org.rapidoidx.compile.impl.EcjCompiler;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
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
