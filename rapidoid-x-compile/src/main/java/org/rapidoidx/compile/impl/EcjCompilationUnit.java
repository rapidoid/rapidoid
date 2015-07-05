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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class EcjCompilationUnit implements ICompilationUnit {

	private static final Pattern PACKAGE_PATTERN = Pattern.compile("package\\s+([\\w\\.]+?)\\s*;");

	private static final Pattern PUBLIC_TYPE_PATTERN = Pattern
			.compile("public(?:\\s+(?:abstract|final))?\\s+(?:class|enum|interface|\\@interface)\\s+(\\w+?)\\b");

	private static final char[][] DEFAULT_PACKAGE = new char[0][];

	private final String packageName;
	private final String className;
	private final String source;

	public EcjCompilationUnit(String packageName, String className, String source) {
		this.packageName = packageName;
		this.className = className;
		this.source = source;
	}

	public EcjCompilationUnit(String source) {
		this.packageName = inferPackageName(source);
		this.className = inferClassName(source);
		this.source = source;
	}

	public char[] getFileName() {
		return (className + ".java").toCharArray();
	}

	public char[] getContents() {
		return source.toCharArray();
	}

	public char[] getMainTypeName() {
		return className.toCharArray();
	}

	public char[][] getPackageName() {
		return !packageName.isEmpty() ? toCharArrays(packageName.split("\\.")) : DEFAULT_PACKAGE;
	}

	public boolean ignoreOptionalProblems() {
		return false;
	}

	public static String inferClassName(String src) {
		Matcher m = PUBLIC_TYPE_PATTERN.matcher(src);
		if (m.find()) {
			return m.group(1);
		} else {
			throw U.rte("Couldn't find/infer the compilation unit name from the source!");
		}
	}

	private static String inferPackageName(String src) {
		Matcher m = PACKAGE_PATTERN.matcher(src);
		if (m.find()) {
			return m.group(1);
		} else {
			return ""; // default package
		}
	}

	private static char[][] toCharArrays(String[] arr) {
		char[][] charArr = new char[arr.length][];

		for (int i = 0; i < arr.length; i++) {
			charArr[i] = arr[i].toCharArray();
		}

		return charArr;
	}

}
