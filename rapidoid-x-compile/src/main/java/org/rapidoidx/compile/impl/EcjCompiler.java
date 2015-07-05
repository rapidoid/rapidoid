package org.rapidoidx.compile.impl;

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

import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.U;
import org.rapidoidx.compile.Compilation;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class EcjCompiler implements ICompilerRequestor {

	private final Compiler compiler;

	private EcjCompilation currentCompilation;

	public EcjCompiler() {
		this(new SimpleNameEnvironment(), new DefaultProblemFactory());
	}

	public EcjCompiler(INameEnvironment environment, IProblemFactory problemFactory) {
		String version = System.getProperty("java.version").substring(0, 3);
		CompilerOptions options = new CompilerOptions(U.map(CompilerOptions.OPTION_Source, version,
				CompilerOptions.OPTION_TargetPlatform, version));
		compiler = new Compiler(environment, new ErrorPolicy(), options, this, problemFactory, null, null);
	}

	public synchronized Compilation compile(ICompilationUnit[] units) {
		currentCompilation = new EcjCompilation();

		compiler.compile(units);

		EcjCompilation comp = currentCompilation;
		currentCompilation = null;
		return comp;
	}

	@Override
	public synchronized void acceptResult(CompilationResult result) {
		currentCompilation.addResult(result);
	}

}
