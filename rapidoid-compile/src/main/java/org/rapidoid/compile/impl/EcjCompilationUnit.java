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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.rapidoid.util.U;

public class EcjCompilationUnit implements ICompilationUnit {

	private static final Pattern PUBLIC_CLASS_PATTERN = Pattern.compile("public\\s+class\\s+(\\w+?)\\b");

	private final String className;
	private final String source;

	public EcjCompilationUnit(String className, String source) {
		this.className = className;
		this.source = source;
	}

	public EcjCompilationUnit(String source) {
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
		// FIXME package name
		return null;
	}

	public boolean ignoreOptionalProblems() {
		return false;
	}

	private static String inferClassName(String src) {
		Matcher m = PUBLIC_CLASS_PATTERN.matcher(src);
		if (m.find()) {
			return m.group(1);
		} else {
			throw U.rte("Couldn't find/infer the public class name from the source!");
		}
	}

}
