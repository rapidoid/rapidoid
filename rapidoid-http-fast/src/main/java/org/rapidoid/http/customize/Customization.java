package org.rapidoid.http.customize;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Config;
import org.rapidoid.setup.My;
import org.rapidoid.u.U;

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

	private volatile ObjectMapper jackson;

	public Customization(String name, Config appConfig, Config serverConfig) {
		this.name = name;
		this.appConfig = appConfig;
		this.serverConfig = serverConfig;
		reset();
	}

	public void reset() {
		staticFilesPath = null;
		errorHandler = null;
		viewRenderer = null;
		pageRenderer = null;
		jsonResponseRenderer = null;
		beanParameterFactory = null;
		loginProvider = null;
		rolesProvider = null;
		validator = null;
		jackson = null;
	}

	public void staticFilesPath(String... staticFilesPath) {
		this.staticFilesPath = staticFilesPath;
	}

	public String[] staticFilesPath() {
		return U.or(staticFilesPath, My.getStaticFilesPath());
	}

	public ErrorHandler errorHandler() {
		return U.or(errorHandler, My.getErrorHandler());
	}

	public void errorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public ViewRenderer viewRenderer() {
		return U.or(viewRenderer, My.getViewRenderer());
	}

	public void viewRenderer(ViewRenderer viewRenderer) {
		this.viewRenderer = viewRenderer;
	}

	public JsonResponseRenderer jsonResponseRenderer() {
		return U.or(jsonResponseRenderer, My.getJsonResponseRenderer());
	}

	public void jsonResponseRenderer(JsonResponseRenderer jsonResponseRenderer) {
		this.jsonResponseRenderer = jsonResponseRenderer;
	}

	public BeanParameterFactory beanParameterFactory() {
		return U.or(beanParameterFactory, My.getBeanParameterFactory());
	}

	public void beanParameterFactory(BeanParameterFactory beanParameterFactory) {
		this.beanParameterFactory = beanParameterFactory;
	}

	public void validator(BeanValidator validator) {
		this.validator = validator;
	}

	public void jackson(ObjectMapper jackson) {
		this.jackson = jackson;
	}

	public LoginProvider loginProvider() {
		return U.or(loginProvider, My.getLoginProvider());
	}

	public void loginProvider(LoginProvider loginProvider) {
		this.loginProvider = loginProvider;
	}

	public RolesProvider rolesProvider() {
		return U.or(rolesProvider, My.getRolesProvider());
	}

	public void rolesProvider(RolesProvider rolesProvider) {
		this.rolesProvider = rolesProvider;
	}

	public PageRenderer pageRenderer() {
		return U.or(pageRenderer, My.getPageRenderer());
	}

	public void pageRenderer(PageRenderer pageRenderer) {
		this.pageRenderer = pageRenderer;
	}

	public BeanValidator validator() {
		return U.or(validator, My.getValidator());
	}

	public ObjectMapper jackson() {
		return U.or(jackson, My.jackson());
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
}
