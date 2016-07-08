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
import org.rapidoid.data.JSON;
import org.rapidoid.http.customize.*;

@Authors("Nikolche Mihajlovski")
@Since("5.1.7")
public class Defaults extends RapidoidThing {

	private static final String[] staticFilesPath;
	private static final ErrorHandler errorHandler;
	private static final JsonResponseRenderer jsonResponseRenderer;
	private static final BeanParameterFactory beanParameterFactory;
	private static final BeanValidator validator;
	private static final LoginProvider loginProvider;
	private static final RolesProvider rolesProvider;
	private static final PageRenderer pageRenderer;
	private static final ViewRenderer viewRenderer;
	private static final ObjectMapper jackson;

	static {
		staticFilesPath = new String[]{"static", "public", "default/static", "default/public"};
		errorHandler = new DefaultErrorHandler();
		viewRenderer = new DefaultViewRenderer();
		pageRenderer = new DefaultPageRenderer();
		jsonResponseRenderer = new DefaultJsonResponseRenderer();
		beanParameterFactory = new DefaultBeanParameterFactory();
		loginProvider = new DefaultLoginProvider();
		rolesProvider = new DefaultRolesProvider();
		validator = new DefaultBeanValidator();
		jackson = JSON.newMapper();
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
