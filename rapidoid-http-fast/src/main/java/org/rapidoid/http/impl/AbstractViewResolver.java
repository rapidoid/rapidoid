package org.rapidoid.http.impl;

/*
 * #%L
 * rapidoid-http-fast
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.http.customize.ResourceLoader;
import org.rapidoid.http.customize.ViewResolver;
import org.rapidoid.lambda.Customizer;
import org.rapidoid.lambda.Mapper;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public abstract class AbstractViewResolver<T> extends RapidoidThing implements ViewResolver {

	private volatile Customizer<T> customizer;

	private volatile String extension = ".html";

	protected abstract T createViewFactory(ResourceLoader templateLoader);

	private final Map<ResourceLoader, T> factoriesPerLoader = Coll.autoExpandingMap(
		new Mapper<ResourceLoader, T>() {
			@Override
			public T map(ResourceLoader templateLoader) throws Exception {
				return customize(createViewFactory(templateLoader));
			}
		});

	protected T getViewFactory(ResourceLoader templateLoader) {
		return factoriesPerLoader.get(templateLoader);
	}

	protected String filename(String viewName) {
		return viewName + extension;
	}

	public T customize(T target) {
		return customizer != null ? customizer.customize(target) : target;
	}

	public Customizer<T> getCustomizer() {
		return customizer;
	}

	public void setCustomizer(Customizer<T> customizer) {
		this.customizer = customizer;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public synchronized void reset() {
		factoriesPerLoader.clear();
	}

}
