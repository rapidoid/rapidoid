package org.rapidoid.web;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.aop.AOP;
import org.rapidoid.entity.AppClasspathEntitiesPlugin;
import org.rapidoid.log.Log;
import org.rapidoid.plugins.Plugins;
import org.rapidoid.plugins.templates.MustacheTemplatesPlugin;
import org.rapidoid.security.annotation.*;

/*
 * #%L
 * rapidoid-web
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
public class RapidoidWebModule {

	static {
		Log.info("Initializing the Rapidoid Web module");

		Plugins.register(new MustacheTemplatesPlugin());
		Plugins.register(new AppClasspathEntitiesPlugin());

		AOP.reset();

		AOP.intercept(new AuthInterceptor(), Admin.class, Manager.class, Moderator.class, LoggedIn.class, DevMode.class,
				Role.class, HasRole.class);
	}

}
