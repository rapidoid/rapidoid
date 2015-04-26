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

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Statement;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.compile.impl.EcjCompilationUnit;
import org.rapidoid.util.D;

@Authors("Nikolche Mihajlovski")
@Since("2.1.0")
public class Parse {

	public static CompilationUnit unit(String source) {
		String unitName = EcjCompilationUnit.inferClassName(source) + ".java";
		ASTParser parser = parser(ASTParser.K_COMPILATION_UNIT, unitName, source);

		ASTNode ast = parser.createAST(null);

		if (ast instanceof CompilationUnit) {
			return (CompilationUnit) ast;
		} else {
			return null;
		}
	}

	public static Statement statements(String source) {
		ASTParser parser = parser(ASTParser.K_STATEMENTS, "(statements)", source);

		ASTNode ast = parser.createAST(null);
		D.print(ast.getClass());

		if (ast instanceof Statement) {
			return (Statement) ast;
		} else {
			return null;
		}
	}

	public static Expression expression(String source) {
		ASTParser parser = parser(ASTParser.K_EXPRESSION, "(expression)", source);

		ASTNode ast = parser.createAST(null);

		if (ast instanceof Expression) {
			return (Expression) ast;
		} else {
			return null;
		}
	}

	private static ASTParser parser(int kind, String unitName, String source) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);

		parser.setUnitName(unitName);
		parser.setSource(source.toCharArray());
		parser.setKind(kind);
		parser.setResolveBindings(true);

		return parser;
	}

}
