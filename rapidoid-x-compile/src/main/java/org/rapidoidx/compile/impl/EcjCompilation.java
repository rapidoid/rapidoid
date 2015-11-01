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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.io.CustomizableClassLoader;
import org.rapidoid.lambda.Lmbd;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoidx.compile.CodeDiagnostic;
import org.rapidoidx.compile.Compilation;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class EcjCompilation implements Compilation {

	private final Map<String, byte[]> classes = U.map();

	private final List<CodeDiagnostic> errors = U.list();

	private final List<CodeDiagnostic> warnings = U.list();

	private final CustomizableClassLoader loader;

	public EcjCompilation(Predicate<String> allowed) {
		loader = new CustomizableClassLoader(Lmbd.mapper(classes), allowed, false);
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
