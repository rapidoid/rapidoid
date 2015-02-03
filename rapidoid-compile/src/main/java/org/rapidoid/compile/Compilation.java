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

import java.util.List;
import java.util.Set;

import org.rapidoid.annotation.Authors;

@Authors("Nikolche Mihajlovski")
public interface Compilation {

	Class<?> loadClass(String fullClassName) throws ClassNotFoundException;

	Set<Class<?>> loadClasses() throws ClassNotFoundException;

	Set<String> getClassNames();

	boolean hasErrors();

	boolean hasWarnings();

	List<CodeDiagnostic> getErrors();

	List<CodeDiagnostic> getWarnings();

}
