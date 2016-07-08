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

	private static volatile String[] staticFilesPath;
	private static volatile ErrorHandler errorHandler;
	private static volatile JsonResponseRenderer jsonResponseRenderer;
	private static volatile BeanParameterFactory beanParameterFactory;
	private static volatile BeanValidator validator;
	private static volatile LoginProvider loginProvider;
	private static volatile RolesProvider rolesProvider;
	private static volatile PageRenderer pageRenderer;
	private static volatile ViewRenderer viewRenderer;
	private static volatile ObjectMapper jackson;

	static {
		reset();
	}

	public static void reset() {
		staticFilesPath = Defaults.staticFilesPath();
		errorHandler = Defaults.errorHandler();
		viewRenderer = Defaults.viewRenderer();
		pageRenderer = Defaults.pageRenderer();
		jsonResponseRenderer = Defaults.jsonResponseRenderer();
		beanParameterFactory = Defaults.beanParameterFactory();
		loginProvider = Defaults.loginProvider();
		rolesProvider = Defaults.rolesProvider();
		validator = Defaults.validator();
		jackson = Defaults.jackson();
	}

	public static void staticFilesPath(String... staticFilesPath) {
		My.staticFilesPath = staticFilesPath;
	}

	public static void errorHandler(ErrorHandler errorHandler) {
		My.errorHandler = errorHandler;
	}

	public static void jsonResponseRenderer(JsonResponseRenderer jsonResponseRenderer) {
		My.jsonResponseRenderer = jsonResponseRenderer;
	}

	public static void beanParameterFactory(BeanParameterFactory beanParameterFactory) {
		My.beanParameterFactory = beanParameterFactory;
	}

	public static void validator(BeanValidator validator) {
		My.validator = validator;
	}

	public static void loginProvider(LoginProvider loginProvider) {
		My.loginProvider = loginProvider;
	}

	public static void rolesProvider(RolesProvider rolesProvider) {
		My.rolesProvider = rolesProvider;
	}

	public static void pageRenderer(PageRenderer pageRenderer) {
		My.pageRenderer = pageRenderer;
	}

	public static void viewRenderer(ViewRenderer viewRenderer) {
		My.viewRenderer = viewRenderer;
	}

	public static void jackson(ObjectMapper jackson) {
		My.jackson = jackson;
	}

	public static String[] staticFilesPath() {
		return staticFilesPath;
	}

	public static ErrorHandler errorHandler() {
		return errorHandler;
	}

	public static JsonResponseRenderer jsonResponseRenderer() {
		return jsonResponseRenderer;
	}

	public static BeanParameterFactory beanParameterFactory() {
		return beanParameterFactory;
	}

	public static BeanValidator validator() {
		return validator;
	}

	public static LoginProvider loginProvider() {
		return loginProvider;
	}

	public static RolesProvider rolesProvider() {
		return rolesProvider;
	}

	public static PageRenderer pageRenderer() {
		return pageRenderer;
	}

	public static ViewRenderer viewRenderer() {
		return viewRenderer;
	}

	public static ObjectMapper jackson() {
		return jackson;
	}

}
