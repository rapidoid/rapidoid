package org.rapidoid.config;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.List;
import java.util.Map;
import java.util.Properties;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public interface Config extends BasicConfig {

	@SuppressWarnings("unchecked")
	@Override
	Config sub(String... keys);

	Config sub(List<String> keys);

	Object get(String key);

	boolean is(String key);

	@Override
	Map<String, Object> toMap();

	void clear();

	void set(String key, Object value);

	void set(String key, Object value, boolean overridenByEnv);

	void remove(String key);

	void assign(Map<String, Object> entries);

	boolean isEmpty();

	void update(Map<String, ?> entries);

	@SuppressWarnings("unchecked")
	void update(Map<String, ?> entries, boolean overridenByEnv);

	Config root();

	Config parent();

	List<String> keys();

	Map<String, String> toFlatMap();

	Properties toProperties();

	BasicConfig or(Config alternative);

	String getFilenameBase();

	Config setFilenameBase(String filenameBase);

	String getPath();

	Config setPath(String path);

	void args(List<String> args);

	void applyTo(Object target);

	void reset();

	void invalidate();

	boolean useBuiltInDefaults();

	boolean isInitialized();

}
