package org.rapidoid.http.customize.defaults;

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
import org.rapidoid.data.JSON;
import org.rapidoid.env.Env;
import org.rapidoid.http.HttpWrapper;
import org.rapidoid.http.customize.*;
import org.rapidoid.render.Templates;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.u.U;

import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.1.7")
public class Defaults extends RapidoidThing {

	private static final String[] staticFilesPath = {"static", "default/static"};

	private static final HttpWrapper[] wrappers = {};

	private static final ErrorHandler errorHandler = new DefaultErrorHandler();

	private static final JsonResponseRenderer jsonResponseRenderer = new DefaultJsonResponseRenderer();

	private static final BeanParameterFactory beanParameterFactory = new DefaultBeanParameterFactory();

	private static final BeanValidator validator = new DefaultBeanValidator();

	private static final LoginProvider loginProvider = new DefaultLoginProvider();

	private static final RolesProvider rolesProvider = new DefaultRolesProvider();

	private static final PageDecorator pageDecorator = new DefaultPageDecorator();

	private static final ResourceLoader templateLoader = new DefaultTemplateLoader(Templates.DEFAULT_PATH);

	private static final ViewResolver viewResolver = new DefaultViewResolver();

	private static final ObjectMapper jackson = JSON.newMapper();

	private static final EntityManagerProvider entityManagerProvider = null;

	private static final EntityManagerFactoryProvider entityManagerFactoryProvider = new DefaultEntityManagerFactoryProvider();

	private static final JsonRequestBodyParser jsonRequestBodyParser = new DefaultJsonRequestBodyParser();

	private static final SessionManager sessionManager = new DefaultSessionManager();

	private static final StaticFilesSecurity staticFilesSecurity = new DefaultStaticFilesSecurity();

	public static String[] staticFilesPath() {
		List<String> path = U.list(staticFilesPath);

		if (Env.isInitialized() && Env.dev()) {
			path.addAll(0, ClasspathUtil.getClasspathStaticFolders());
		}

		return U.arrayOf(path);
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

	public static PageDecorator pageDecorator() {
		return pageDecorator;
	}

	public static ViewResolver viewResolver() {
		return viewResolver;
	}

	public static ObjectMapper jackson() {
		return jackson;
	}

	public static EntityManagerProvider entityManagerProvider() {
		return entityManagerProvider;
	}

	public static EntityManagerFactoryProvider entityManagerFactoryProvider() {
		return entityManagerFactoryProvider;
	}

	public static String[] templatesPath() {
		return Templates.getPath();
	}

	public static JsonRequestBodyParser jsonRequestBodyParser() {
		return jsonRequestBodyParser;
	}

	public static SessionManager sessionManager() {
		return sessionManager;
	}

	public static StaticFilesSecurity staticFilesSecurity() {
		return staticFilesSecurity;
	}

	public static HttpWrapper[] wrappers() {
		return wrappers;
	}

	public static ResourceLoader templateLoader() {
		return templateLoader;
	}
}
