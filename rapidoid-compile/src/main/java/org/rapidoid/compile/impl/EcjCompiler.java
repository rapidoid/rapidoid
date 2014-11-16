package org.rapidoid.compile.impl;

/*
 * #%L
 * rapidoid-compile
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.rapidoid.compile.Compilation;
import org.rapidoid.util.U;

public class EcjCompiler implements ICompilerRequestor {

	private final Compiler compiler;

	private EcjCompilation currentCompilation;

	public EcjCompiler() {
		this(new SimpleNameEnvironment(), new DefaultProblemFactory());
	}

	public EcjCompiler(INameEnvironment environment, IProblemFactory problemFactory) {
		CompilerOptions options = new CompilerOptions(U.map(CompilerOptions.OPTION_Source, "1.8"));
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
