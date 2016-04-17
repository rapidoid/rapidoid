package org.rapidoid.http.customize;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.config.Config;

/*
 * #%L
 * rapidoid-http-fast
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Customization extends RapidoidThing {

	public static final String[] DEFAULT_STATIC_FILES_LOCATIONS = {"static", "public", "default/static", "default/public"};

	private static final String PAGE_RENDERER = "org.rapidoid.web.DefaultPageRenderer";

	private final String name;

	private final Config appConfig;

	private final Config serverConfig;

	private volatile String[] staticFilesPath;

	private volatile ErrorHandler errorHandler;

	private volatile ViewRenderer viewRenderer;

	private volatile PageRenderer pageRenderer;

	private volatile JsonResponseRenderer jsonResponseRenderer;

	private volatile BeanParameterFactory beanParameterFactory;

	private volatile LoginProvider loginProvider;

	private volatile RolesProvider rolesProvider;

	private volatile BeanValidator validator;

	public Customization(String name, Config appConfig, Config serverConfig) {
		this.name = name;
		this.appConfig = appConfig;
		this.serverConfig = serverConfig;
		reset();
	}

	public void reset() {
		staticFilesPath = DEFAULT_STATIC_FILES_LOCATIONS;
		errorHandler = new DefaultErrorHandler(this);
		viewRenderer = new DefaultViewRenderer();
		pageRenderer = optionalPageRenderer();
		jsonResponseRenderer = new DefaultJsonResponseRenderer();
		beanParameterFactory = new DefaultBeanParameterFactory();
		loginProvider = new DefaultLoginProvider();
		rolesProvider = new DefaultRolesProvider();
		validator = new DefaultBeanValidator();
	}

	protected PageRenderer optionalPageRenderer() {
		Class<?> rendererCls = Cls.getClassIfExists(PAGE_RENDERER);

		return rendererCls != null ? (PageRenderer) Cls.newInstance(rendererCls, this) : null;
	}

	public void staticFilesPath(String... staticFilesPath) {
		this.staticFilesPath = staticFilesPath;
	}

	public String[] staticFilesPath() {
		return staticFilesPath;
	}

	public ErrorHandler errorHandler() {
		return errorHandler;
	}

	public void errorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public ViewRenderer viewRenderer() {
		return viewRenderer;
	}

	public void viewRenderer(ViewRenderer viewRenderer) {
		this.viewRenderer = viewRenderer;
	}

	public JsonResponseRenderer jsonResponseRenderer() {
		return jsonResponseRenderer;
	}

	public void jsonResponseRenderer(JsonResponseRenderer jsonResponseRenderer) {
		this.jsonResponseRenderer = jsonResponseRenderer;
	}

	public BeanParameterFactory beanParameterFactory() {
		return beanParameterFactory;
	}

	public void beanParameterFactory(BeanParameterFactory beanParameterFactory) {
		this.beanParameterFactory = beanParameterFactory;
	}

	public LoginProvider loginProvider() {
		return loginProvider;
	}

	public void loginProvider(LoginProvider loginProvider) {
		this.loginProvider = loginProvider;
	}

	public RolesProvider rolesProvider() {
		return rolesProvider;
	}

	public void rolesProvider(RolesProvider rolesProvider) {
		this.rolesProvider = rolesProvider;
	}

	public PageRenderer pageRenderer() {
		return pageRenderer;
	}

	public void pageRenderer(PageRenderer pageRenderer) {
		this.pageRenderer = pageRenderer;
	}

	public String name() {
		return name;
	}

	public Config appConfig() {
		return appConfig;
	}

	public Config serverConfig() {
		return serverConfig;
	}

	public BeanValidator validator() {
		return validator;
	}

	public void validator(BeanValidator validator) {
		this.validator = validator;
	}
}
