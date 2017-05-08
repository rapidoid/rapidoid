package org.rapidoid.setup;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HttpWrapper;
import org.rapidoid.http.customize.*;
import org.rapidoid.http.customize.defaults.Defaults;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class My extends RapidoidThing {

	private static final Customization GLOBAL = new Customization("my", null, null);

	static {
		reset();
	}

	public static void reset() {
		synchronized (GLOBAL) {
			GLOBAL.reset();
			GLOBAL.staticFilesPath(Defaults.staticFilesPath());
			GLOBAL.errorHandler(Defaults.errorHandler());
			GLOBAL.viewResolver(Defaults.viewResolver());
			GLOBAL.pageDecorator(Defaults.pageDecorator());
			GLOBAL.jsonResponseRenderer(Defaults.jsonResponseRenderer());
			GLOBAL.jsonRequestBodyParser(Defaults.jsonRequestBodyParser());
			GLOBAL.beanParameterFactory(Defaults.beanParameterFactory());
			GLOBAL.loginProvider(Defaults.loginProvider());
			GLOBAL.rolesProvider(Defaults.rolesProvider());
			GLOBAL.validator(Defaults.validator());
			GLOBAL.jackson(Defaults.jackson());
			GLOBAL.entityManagerFactoryProvider(Defaults.entityManagerFactoryProvider());
			GLOBAL.entityManagerProvider(Defaults.entityManagerProvider());
			GLOBAL.sessionManager(Defaults.sessionManager());
			GLOBAL.staticFilesSecurity(Defaults.staticFilesSecurity());
			GLOBAL.wrappers(Defaults.wrappers());
			GLOBAL.templateLoader(Defaults.templateLoader());
		}
	}

	public static Customization custom() {
		return GLOBAL;
	}

	public static String[] staticFilesPath() {
		return GLOBAL.staticFilesPath();
	}

	public static void staticFilesPath(String... staticFilesPath) {
		GLOBAL.staticFilesPath(staticFilesPath);
	}

	public static String[] templatesPath() {
		return GLOBAL.templatesPath();
	}

	public static void templatesPath(String... templatesPath) {
		GLOBAL.templatesPath(templatesPath);
	}

	public static HttpWrapper[] wrappers() {
		return GLOBAL.wrappers();
	}

	public static void wrappers(HttpWrapper... wrappers) {
		GLOBAL.wrappers(wrappers);
	}

	public static void errorHandler(ErrorHandler errorHandler) {
		GLOBAL.errorHandler(errorHandler);
	}

	public static void jsonResponseRenderer(JsonResponseRenderer jsonResponseRenderer) {
		GLOBAL.jsonResponseRenderer(jsonResponseRenderer);
	}

	public static void beanParameterFactory(BeanParameterFactory beanParameterFactory) {
		GLOBAL.beanParameterFactory(beanParameterFactory);
	}

	public static void validator(BeanValidator validator) {
		GLOBAL.validator(validator);
	}

	public static void loginProvider(LoginProvider loginProvider) {
		GLOBAL.loginProvider(loginProvider);
	}

	public static void rolesProvider(RolesProvider rolesProvider) {
		GLOBAL.rolesProvider(rolesProvider);
	}

	public static void pageDecorator(PageDecorator pageDecorator) {
		GLOBAL.pageDecorator(pageDecorator);
	}

	public static void viewResolver(ViewResolver viewResolver) {
		GLOBAL.viewResolver(viewResolver);
	}

	public static void jackson(ObjectMapper jackson) {
		GLOBAL.jackson(jackson);
	}

	public static ErrorHandler errorHandler() {
		return GLOBAL.errorHandler();
	}

	public static JsonResponseRenderer jsonResponseRenderer() {
		return GLOBAL.jsonResponseRenderer();
	}

	public static BeanParameterFactory beanParameterFactory() {
		return GLOBAL.beanParameterFactory();
	}

	public static BeanValidator validator() {
		return GLOBAL.validator();
	}

	public static LoginProvider loginProvider() {
		return GLOBAL.loginProvider();
	}

	public static RolesProvider rolesProvider() {
		return GLOBAL.rolesProvider();
	}

	public static PageDecorator pageDecorator() {
		return GLOBAL.pageDecorator();
	}

	public static ViewResolver viewResolver() {
		return GLOBAL.viewResolver();
	}

	public static ObjectMapper jackson() {
		return GLOBAL.jackson();
	}

	public static EntityManagerProvider entityManagerProvider() {
		return GLOBAL.entityManagerProvider();
	}

	public static void entityManagerProvider(EntityManagerProvider entityManagerProvider) {
		GLOBAL.entityManagerProvider(entityManagerProvider);
	}

	public static EntityManagerFactoryProvider entityManagerFactoryProvider() {
		return GLOBAL.entityManagerFactoryProvider();
	}

	public static void entityManagerFactoryProvider(EntityManagerFactoryProvider entityManagerFactoryProvider) {
		GLOBAL.entityManagerFactoryProvider(entityManagerFactoryProvider);
	}

	public static JsonRequestBodyParser jsonRequestBodyParser() {
		return GLOBAL.jsonRequestBodyParser();
	}

	public static void jsonRequestBodyParser(JsonRequestBodyParser jsonRequestBodyParser) {
		GLOBAL.jsonRequestBodyParser(jsonRequestBodyParser);
	}

	public static SessionManager sessionManager() {
		return GLOBAL.sessionManager();
	}

	public static Customization sessionManager(SessionManager sessionManager) {
		return GLOBAL.sessionManager(sessionManager);
	}

	public static StaticFilesSecurity staticFilesSecurity() {
		return GLOBAL.staticFilesSecurity();
	}

	public static Customization staticFilesSecurity(StaticFilesSecurity staticFilesSecurity) {
		return GLOBAL.staticFilesSecurity(staticFilesSecurity);
	}

	public static OnError error(Class<? extends Throwable> error) {
		return new OnError(GLOBAL, error);
	}

	public static ResourceLoader templateLoader() {
		return GLOBAL.templateLoader();
	}

	public static Customization templateLoader(ResourceLoader templateLoader) {
		return GLOBAL.templateLoader(templateLoader);
	}

}
