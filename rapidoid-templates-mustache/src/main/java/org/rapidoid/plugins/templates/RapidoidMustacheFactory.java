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

import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.io.Res;
import org.rapidoid.log.Log;
import org.rapidoid.util.U;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.DefaultMustacheVisitor;
import com.github.mustachejava.FragmentKey;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheException;
import com.github.mustachejava.MustacheVisitor;
import com.github.mustachejava.ObjectHandler;
import com.github.mustachejava.TemplateContext;
import com.github.mustachejava.codes.ValueCode;
import com.google.common.cache.LoadingCache;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class RapidoidMustacheFactory extends DefaultMustacheFactory {

	private static final String SM = "{{";

	private static final String EM = "}}";

	private static final ObjectHandler CUSTOM_OBJECT_HANDLER = new CustomObjectHandler();

	private volatile LoadingCache<String, Mustache> mustacheCache;

	private volatile LoadingCache<FragmentKey, Mustache> lambdaCache;

	private final ThreadLocal<Map<String, Mustache>> partialCache = new ThreadLocal<Map<String, Mustache>>() {
		@Override
		protected Map<String, Mustache> initialValue() {
			return new HashMap<String, Mustache>();
		}
	};

	public RapidoidMustacheFactory() {
		setObjectHandler(CUSTOM_OBJECT_HANDLER);
	}

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
	public Mustache compile(String name) {
		return compileIfChanged(name, false);
	}

	private Mustache compileIfChanged(String filename, boolean partial) {
		Mustache template = mustacheCache.getIfPresent(filename);

		if (template == null) {

			String desc = partial ? "partial" : "template";
			Log.info("Compiling Mustache " + desc, "name", filename);

			Res res = getResource(filename, partial);
			template = customCompile(filename, res);

			res.onChange("mustache", new Runnable() {
				@Override
				public void run() {
					invalidateCache();
				}
			}).trackChanges();

			mustacheCache.put(filename, template);
		}

		return template;
	}

	@Override
	public Mustache compilePartial(String name) {
		return compileIfChanged(name, true);
	}

	@Override
	public Mustache compile(Reader reader, String file, String sm, String em) {
		throw U.notExpected();
	}

	private Mustache customCompile(String filename, Res resource) {
		Map<String, Mustache> cache = partialCache.get();
		try {
			Mustache mustache = cache.get(filename);
			if (mustache == null) {
				mustache = mc.compile(resource.getReader(), filename, SM, EM);
				cache.put(filename, mustache);
				mustache.init();
			}

			return mustache;
		} finally {
			cache.remove(filename);
		}
	}

	@Override
	public Mustache compile(Reader reader, String name) {
		return super.compile(reader, name, SM, EM);
	}

	public void invalidateCache() {
		Log.info("Invalidating Mustache cache");
		mustacheCache.invalidateAll();
		lambdaCache.invalidateAll();
	}

	@Override
	public Reader getReader(String resourceName) {
		return getResource(resourceName, false).getReader();
	}

	private Res getResource(String filename, boolean partial) {
		String sub = partial ? "/partials/" : "/templates/";
		String firstFile = Conf.rootPath() + sub + filename;
		String defaultFile = Conf.rootPathDefault() + sub + filename;
		return Res.from(filename, true, firstFile, defaultFile);
	}

	@Override
	public MustacheVisitor createMustacheVisitor() {
		return new DefaultMustacheVisitor(this) {
			@Override
			public void value(TemplateContext tc, String var, boolean encoded) {
				list.add(new ValueCode(tc, df, var, encoded) {
					@Override
					public Writer execute(Writer writer, Object[] scopes) {
						try {
							final Object object = get(scopes);
							if (object == null) {
								identity(writer);
								return writer;
							}
							return super.execute(writer, scopes);
						} catch (Exception e) {
							throw new MustacheException("Failed to get value for " + name, e, tc);
						}
					}
				});
			}
		};
	}

}
