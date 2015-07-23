package org.rapidoid.test;

/*
 * #%L
 * rapidoid-test-appctx-helper
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import org.junit.After;
import org.junit.Before;
import org.rapidoid.apps.AppClasspathEntitiesPlugin;
import org.rapidoid.apps.Applications;
import org.rapidoid.apps.RootApplication;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.plugins.Plugins;

/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public abstract class ContextAwareTest extends TestCommons {

	@Before
	public void openContext() {
		RootApplication app = Applications.root();
		Applications.main().setDefaultApp(app);

		Ctxs.open();
		Ctxs.ctx().setApp(app);

		Plugins.register(new AppClasspathEntitiesPlugin());
	}

	@After
	public void closeContext() {
		Ctxs.close();
	}

}
