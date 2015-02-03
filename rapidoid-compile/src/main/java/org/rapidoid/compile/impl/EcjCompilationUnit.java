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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.rapidoid.util.U;

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
			throw U.rte("Couldn't find/infer the public class name from the source!");
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
