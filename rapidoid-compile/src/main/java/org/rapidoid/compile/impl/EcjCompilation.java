package org.rapidoid.compile.impl;

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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.rapidoid.compile.CodeDiagnostic;
import org.rapidoid.compile.Compilation;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.util.CustomizableClassLoader;
import org.rapidoid.util.Log;
import org.rapidoid.util.U;

public class EcjCompilation implements Compilation {

	private final Map<String, byte[]> classes = U.map();

	private final List<CodeDiagnostic> errors = U.list();

	private final List<CodeDiagnostic> warnings = U.list();

	private final CustomizableClassLoader loader;

	public EcjCompilation(Predicate<String> allowed) {
		loader = new CustomizableClassLoader(U.mapper(classes), allowed, false);
	}

	@SuppressWarnings("unchecked")
	public EcjCompilation() {
		this((Predicate<String>) Predicate.ALWAYS_TRUE);
	}

	@Override
	public Class<?> loadClass(String fullClassName) throws ClassNotFoundException {
		return loader.loadClass(fullClassName);
	}

	@Override
	public Set<Class<?>> loadClasses() throws ClassNotFoundException {
		Set<Class<?>> loadedClasses = U.set();

		for (String className : classes.keySet()) {
			loadedClasses.add(loadClass(className));
		}

		return loadedClasses;
	}

	@Override
	public Set<String> getClassNames() {
		return classes.keySet();
	}

	@Override
	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	@Override
	public boolean hasWarnings() {
		return !warnings.isEmpty();
	}

	@Override
	public List<CodeDiagnostic> getErrors() {
		return errors;
	}

	@Override
	public List<CodeDiagnostic> getWarnings() {
		return warnings;
	}

	private static CodeDiagnostic diagnostic(CategorizedProblem problem) {
		CodeDiagnostic diagnostic = new CodeDiagnostic();

		diagnostic.message = problem.getMessage();
		diagnostic.filename = new String(problem.getOriginatingFileName());
		diagnostic.line = problem.getSourceLineNumber();
		diagnostic.start = problem.getSourceStart();
		diagnostic.end = problem.getSourceEnd();

		return diagnostic;
	}

	@Override
	public String toString() {
		return "EcjCompilation [classes=" + classes.keySet() + ", errors=" + errors + ", warnings=" + warnings + "]";
	}

	public void addResult(CompilationResult result) {
		for (ClassFile cls : result.getClassFiles()) {
			classes.put(U.join(".", cls.getCompoundName()), cls.getBytes());
		}

		CategorizedProblem[] problems = result.getAllProblems();

		if (problems != null) {
			for (CategorizedProblem problem : problems) {
				if (problem.isError()) {
					errors.add(diagnostic(problem));
				} else if (problem.isWarning()) {
					warnings.add(diagnostic(problem));
				} else {
					Log.warn("Unknown problem type!", "problem", problem);
				}
			}
		}
	}

}
