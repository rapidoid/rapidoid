package org.rapidoidx.compile.impl;

/*
 * #%L
 * rapidoid-x-compile
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.IO;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
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
			byte[] classFileBytes = IO.classBytes(fullclassName);
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
	public void cleanup() {}

}
