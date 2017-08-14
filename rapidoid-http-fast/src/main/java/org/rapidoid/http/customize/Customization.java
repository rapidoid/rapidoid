package org.rapidoid.http.customize;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Config;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.http.HttpWrapper;
import org.rapidoid.http.Req;
import org.rapidoid.http.customize.defaults.DefaultTemplateLoader;
import org.rapidoid.http.impl.ErrorHandlerResolver;
import org.rapidoid.setup.My;
import org.rapidoid.u.U;
import org.rapidoid.util.ByType;

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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Customization extends RapidoidThing {

	private final String name;
	private final Customization defaults;
	private final Config config;

	private final ByType<Throwable, ErrorHandler> errorHandlers = ByType.create();

	private final ErrorHandlerResolver errorHandlerResolver = new ErrorHandlerResolver();

	private volatile String[] staticFilesPath;

	private volatile ErrorHandler errorHandler;

	private volatile ViewResolver viewResolver;

	private volatile PageDecorator pageDecorator;

	private volatile JsonResponseRenderer jsonResponseRenderer;

	private volatile JsonRequestBodyParser jsonRequestBodyParser;

	private volatile BeanParameterFactory beanParameterFactory;

	private volatile LoginProvider loginProvider;

	private volatile RolesProvider rolesProvider;

	private volatile BeanValidator validator;

	private volatile ObjectMapper jackson;

	private volatile EntityManagerProvider entityManagerProvider;

	private volatile EntityManagerFactoryProvider entityManagerFactoryProvider;

	private volatile SessionManager sessionManager;

	private volatile StaticFilesSecurity staticFilesSecurity;

	private volatile HttpWrapper[] wrappers;

	private volatile ResourceLoader templateLoader;

	public Customization(String name, Customization defaults, Config config) {
		this.name = name;
		this.defaults = defaults;
		this.config = config;

		reset();
	}

	public synchronized void reset() {
		staticFilesPath = null;
		errorHandler = null;
		viewResolver = null;
		pageDecorator = null;
		jsonResponseRenderer = null;
		beanParameterFactory = null;
		loginProvider = null;
		rolesProvider = null;
		validator = null;
		jackson = null;
		entityManagerProvider = null;
		entityManagerFactoryProvider = null;
		sessionManager = null;
		errorHandlers.reset();
		staticFilesSecurity = null;
		wrappers = null;
		templateLoader = null;
	}

	public static Customization of(Req req) {
		assert inValidContext(req);
		return req != null ? req.custom() : My.custom();
	}

	private static boolean inValidContext(Req req) {
		Ctx ctx = Ctxs.get();
		Req ctxReq = ctx != null ? (Req) ctx.exchange() : null;
		U.must(req == ctxReq, "The customization request (%s) doesn't match the context request (%s)!", req, ctxReq);
		return true;
	}

	public static Customization current() {
		Ctx ctx = Ctxs.get();
		return of(ctx != null ? (Req) ctx.exchange() : null);
	}

	public String name() {
		return name;
	}

	public Customization defaults() {
		return defaults;
	}

	public Config config() {
		return config;
	}

	public String[] staticFilesPath() {
		return staticFilesPath != null || defaults == null ? staticFilesPath : defaults.staticFilesPath();
	}

	public Customization staticFilesPath(String... staticFilesPath) {
		this.staticFilesPath = staticFilesPath;
		return this;
	}

	public String[] templatesPath() {

		if (templateLoader != null || defaults == null) {
			U.must(templateLoader instanceof DefaultTemplateLoader, "A custom template loader was configured!");
			return ((DefaultTemplateLoader) templateLoader).templatesPath();

		} else {
			return defaults.templatesPath();
		}
	}

	public Customization templatesPath(String... templatesPath) {
		this.templateLoader = new DefaultTemplateLoader(templatesPath);
		return this;
	}

	public ErrorHandler errorHandler() {
		return errorHandler != null || defaults == null ? errorHandler : defaults.errorHandler();
	}

	public Customization errorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
		return this;
	}

	public ViewResolver viewResolver() {
		return viewResolver != null || defaults == null ? viewResolver : defaults.viewResolver();
	}

	public Customization viewResolver(ViewResolver viewResolver) {
		this.viewResolver = viewResolver;
		return this;
	}

	public PageDecorator pageDecorator() {
		return pageDecorator != null || defaults == null ? pageDecorator : defaults.pageDecorator();
	}

	public Customization pageDecorator(PageDecorator pageDecorator) {
		this.pageDecorator = pageDecorator;
		return this;
	}

	public JsonResponseRenderer jsonResponseRenderer() {
		return jsonResponseRenderer != null || defaults == null ? jsonResponseRenderer : defaults.jsonResponseRenderer();
	}

	public Customization jsonResponseRenderer(JsonResponseRenderer jsonResponseRenderer) {
		this.jsonResponseRenderer = jsonResponseRenderer;
		return this;
	}

	public BeanParameterFactory beanParameterFactory() {
		return beanParameterFactory != null || defaults == null ? beanParameterFactory : defaults.beanParameterFactory();
	}

	public Customization beanParameterFactory(BeanParameterFactory beanParameterFactory) {
		this.beanParameterFactory = beanParameterFactory;
		return this;
	}

	public LoginProvider loginProvider() {
		return loginProvider != null || defaults == null ? loginProvider : defaults.loginProvider();
	}

	public Customization loginProvider(LoginProvider loginProvider) {
		this.loginProvider = loginProvider;
		return this;
	}

	public RolesProvider rolesProvider() {
		return rolesProvider != null || defaults == null ? rolesProvider : defaults.rolesProvider();
	}

	public Customization rolesProvider(RolesProvider rolesProvider) {
		this.rolesProvider = rolesProvider;
		return this;
	}

	public BeanValidator validator() {
		return validator != null || defaults == null ? validator : defaults.validator();
	}

	public Customization validator(BeanValidator validator) {
		this.validator = validator;
		return this;
	}

	public ObjectMapper jackson() {
		return jackson != null || defaults == null ? jackson : defaults.jackson();
	}

	public Customization jackson(ObjectMapper jackson) {
		this.jackson = jackson;
		return this;
	}

	public EntityManagerProvider entityManagerProvider() {
		return entityManagerProvider != null || defaults == null ? entityManagerProvider : defaults.entityManagerProvider();
	}

	public Customization entityManagerProvider(EntityManagerProvider entityManagerProvider) {
		this.entityManagerProvider = entityManagerProvider;
		return this;
	}

	public EntityManagerFactoryProvider entityManagerFactoryProvider() {
		return entityManagerFactoryProvider != null || defaults == null ? entityManagerFactoryProvider : defaults.entityManagerFactoryProvider();
	}

	public Customization entityManagerFactoryProvider(EntityManagerFactoryProvider entityManagerFactoryProvider) {
		this.entityManagerFactoryProvider = entityManagerFactoryProvider;
		return this;
	}

	public JsonRequestBodyParser jsonRequestBodyParser() {
		return jsonRequestBodyParser != null || defaults == null ? jsonRequestBodyParser : defaults.jsonRequestBodyParser();
	}

	public Customization jsonRequestBodyParser(JsonRequestBodyParser jsonRequestBodyParser) {
		this.jsonRequestBodyParser = jsonRequestBodyParser;
		return this;
	}

	public SessionManager sessionManager() {
		return sessionManager != null || defaults == null ? sessionManager : defaults.sessionManager();
	}

	public Customization sessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
		return this;
	}

	public ByType<Throwable, ErrorHandler> errorHandlers() {
		return errorHandlers;
	}

	public ErrorHandler findErrorHandlerByType(Throwable error) {
		return errorHandlerResolver.findErrorHandlerByType(this, error);
	}

	public StaticFilesSecurity staticFilesSecurity() {
		return staticFilesSecurity != null || defaults == null ? staticFilesSecurity : defaults.staticFilesSecurity();
	}

	public Customization staticFilesSecurity(StaticFilesSecurity staticFilesSecurity) {
		this.staticFilesSecurity = staticFilesSecurity;
		return this;
	}

	public HttpWrapper[] wrappers() {
		return wrappers != null || defaults == null ? wrappers : defaults.wrappers();
	}

	public Customization wrappers(HttpWrapper... wrappers) {
		this.wrappers = wrappers;
		return this;
	}

	public ResourceLoader templateLoader() {
		return templateLoader != null || defaults == null ? templateLoader : defaults.templateLoader();
	}

	public Customization templateLoader(ResourceLoader templateLoader) {
		this.templateLoader = templateLoader;
		return this;
	}

	@Override
	public String toString() {
		return "Customization{" +
			"name='" + name + '\'' +
			(defaults != null ? ", defaults=" + defaults : "") +
			'}';
	}
}
