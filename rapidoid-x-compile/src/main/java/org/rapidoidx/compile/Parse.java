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

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Statement;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoidx.compile.impl.EcjCompilationUnit;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
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
