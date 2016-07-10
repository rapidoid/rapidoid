package org.rapidoid.setup;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.customize.*;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class My extends RapidoidThing {

	private static final Customization GLOBAL = new Customization("my", null, null, null);

	static {
		reset();
	}

	public static void reset() {
		synchronized (GLOBAL) {
			GLOBAL.reset();
			GLOBAL.staticFilesPath(Defaults.staticFilesPath());
			GLOBAL.errorHandler(Defaults.errorHandler());
			GLOBAL.viewRenderer(Defaults.viewRenderer());
			GLOBAL.pageRenderer(Defaults.pageRenderer());
			GLOBAL.jsonResponseRenderer(Defaults.jsonResponseRenderer());
			GLOBAL.beanParameterFactory(Defaults.beanParameterFactory());
			GLOBAL.loginProvider(Defaults.loginProvider());
			GLOBAL.rolesProvider(Defaults.rolesProvider());
			GLOBAL.validator(Defaults.validator());
			GLOBAL.jackson(Defaults.jackson());
			GLOBAL.entityManagerFactoryProvider(Defaults.entityManagerFactoryProvider());
			GLOBAL.entityManagerProvider(Defaults.entityManagerProvider());
			GLOBAL.templatesPath(Defaults.templatesPath());
		}
	}

	public static Customization custom() {
		return GLOBAL;
	}

	public static void staticFilesPath(String... staticFilesPath) {
		GLOBAL.staticFilesPath(staticFilesPath);
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

	public static void pageRenderer(PageRenderer pageRenderer) {
		GLOBAL.pageRenderer(pageRenderer);
	}

	public static void viewRenderer(ViewRenderer viewRenderer) {
		GLOBAL.viewRenderer(viewRenderer);
	}

	public static void jackson(ObjectMapper jackson) {
		GLOBAL.jackson(jackson);
	}

	public static String[] staticFilesPath() {
		return GLOBAL.staticFilesPath();
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

	public static PageRenderer pageRenderer() {
		return GLOBAL.pageRenderer();
	}

	public static ViewRenderer viewRenderer() {
		return GLOBAL.viewRenderer();
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

}
