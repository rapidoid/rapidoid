package org.rapidoid.plugins.templates;

/*
 * #%L
 * rapidoid-templates-mustache
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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
import org.rapidoid.io.IO;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.FragmentKey;
import com.github.mustachejava.Mustache;
import com.google.common.cache.LoadingCache;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class RapidoidMustacheFactory extends DefaultMustacheFactory {

	private volatile LoadingCache<String, Mustache> mustacheCache;

	private volatile LoadingCache<FragmentKey, Mustache> lambdaCache;

	@Override
	protected LoadingCache<String, Mustache> createMustacheCache() {
		mustacheCache = super.createMustacheCache();
		return mustacheCache;
	}

	@Override
	protected LoadingCache<FragmentKey, Mustache> createLambdaCache() {
		lambdaCache = super.createLambdaCache();
		return lambdaCache;
	}

	@Override
	public Mustache compile(String filename) {
		String name = IO.getReadOrDefaultFilename(filename);
		return super.compile(name);
	}

	@Override
	public Mustache compilePartial(String filename) {
		String name = IO.getReadOrDefaultFilename(filename);
		return super.compilePartial(name);
	}

	public void invalidateCache() {
		mustacheCache.invalidateAll();
		lambdaCache.invalidateAll();
	}

}
