package org.rapidoid.setup;

/*
 * #%L
 * rapidoid-http-server
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.customize.*;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class My extends RapidoidThing {

	public static void staticFilesPath(String... staticFilesPath) {
		On.custom().staticFilesPath(staticFilesPath);
		Admin.custom().staticFilesPath(staticFilesPath);
	}

	public static void errorHandler(ErrorHandler errorHandler) {
		On.custom().errorHandler(errorHandler);
		Admin.custom().errorHandler(errorHandler);
	}

	public static void jsonResponseRenderer(JsonResponseRenderer jsonResponseRenderer) {
		On.custom().jsonResponseRenderer(jsonResponseRenderer);
		Admin.custom().jsonResponseRenderer(jsonResponseRenderer);
	}

	public static void beanParameterFactory(BeanParameterFactory beanParameterFactory) {
		On.custom().beanParameterFactory(beanParameterFactory);
		Admin.custom().beanParameterFactory(beanParameterFactory);
	}

	public static void validator(BeanValidator validator) {
		On.custom().validator(validator);
		Admin.custom().validator(validator);
	}

	public static void loginProvider(LoginProvider loginProvider) {
		On.custom().loginProvider(loginProvider);
		Admin.custom().loginProvider(loginProvider);
	}

	public static void rolesProvider(RolesProvider rolesProvider) {
		On.custom().rolesProvider(rolesProvider);
		Admin.custom().rolesProvider(rolesProvider);
	}

	public static void pageRenderer(PageRenderer pageRenderer) {
		On.custom().pageRenderer(pageRenderer);
		Admin.custom().pageRenderer(pageRenderer);
	}

	public static void viewRenderer(ViewRenderer viewRenderer) {
		On.custom().viewRenderer(viewRenderer);
		Admin.custom().viewRenderer(viewRenderer);
	}

}
