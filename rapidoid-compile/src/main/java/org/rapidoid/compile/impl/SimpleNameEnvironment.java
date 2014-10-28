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

import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.rapidoid.util.U;

public class SimpleNameEnvironment implements INameEnvironment {

	@Override
	public NameEnvironmentAnswer findType(char[][] compoundTypeName) {
		String className = U.join(".", compoundTypeName);
		return className != null ? type(className) : null;
	}

	@Override
	public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName) {
		String type = new String(typeName);

		String pkg = U.join(".", packageName);
		String cls = pkg + "." + type;

		return type(cls);
	}

	protected NameEnvironmentAnswer type(String fullclassName) {
		try {
			byte[] classFileBytes = U.classBytes(fullclassName);
			if (classFileBytes == null) {
				return null;
			}
			IBinaryType binaryType = new ClassFileReader(classFileBytes, null);

			AccessRestriction accessRestriction = null;
			NameEnvironmentAnswer answer = new NameEnvironmentAnswer(binaryType, accessRestriction);
			return answer;
		} catch (Exception e) {
			throw U.rte(e);
		}
	}

	@Override
	public boolean isPackage(char[][] parentPackageName, char[] packageName) {
		String pkg = parentPackageName != null ? U.join(".", parentPackageName) + "." + new String(packageName)
				: new String(packageName);

		return isPackage(pkg);
	}

	protected boolean isPackage(String pkg) {
		return true;
	}

	@Override
	public void cleanup() {
	}

}
