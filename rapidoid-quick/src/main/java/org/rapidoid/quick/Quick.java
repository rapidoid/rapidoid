package org.rapidoid.quick;

/*
 * #%L
 * rapidoid-quick
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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.Transactional;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.annotation.Transaction;
import org.rapidoid.aop.AOP;
import org.rapidoid.app.Apps;
import org.rapidoid.app.AuthInterceptor;
import org.rapidoid.app.TransactionInterceptor;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.job.Jobs;
import org.rapidoid.log.Log;
import org.rapidoid.plugins.Plugins;
import org.rapidoid.plugins.cache.guava.GuavaCachePlugin;
import org.rapidoid.plugins.db.hibernate.HibernateDBPlugin;
import org.rapidoid.security.annotation.Admin;
import org.rapidoid.security.annotation.DevMode;
import org.rapidoid.security.annotation.LoggedIn;
import org.rapidoid.security.annotation.Manager;
import org.rapidoid.security.annotation.Moderator;
import org.rapidoid.security.annotation.Role;
import org.rapidoid.security.annotation.Roles;
import org.rapidoid.util.U;
import org.rapidoid.webapp.AppClasspathEntitiesPlugin;
import org.rapidoid.webapp.WebApp;
import org.rapidoid.webapp.WebAppGroup;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class Quick {

	public static void run(WebApp app, String[] args, Object... config) {
		bootstrap(app, args, config);
		serve(app, args, config);
	}

	public static void serve(WebApp app, String[] args, Object... config) {
		Apps.serve(app, args, config);
	}

	@SuppressWarnings("unchecked")
	public static void bootstrap(WebApp app, final String[] args, Object... config) {
		Apps.bootstrap(app, args, config);

		WebAppGroup.main().setDefaultApp(app);
		WebAppGroup.main().register(app);

		Ctx ctx = Ctxs.open("quick");
		ctx.setApp(app);
		Ctxs.setPersisterProvider(new QuickJPA(config));

		Plugins.register(new HibernateDBPlugin());
		Plugins.register(new AppClasspathEntitiesPlugin());
		Plugins.register(new GuavaCachePlugin());

		AOP.reset();
		AOP.intercept(new AuthInterceptor(), Admin.class, Manager.class, Moderator.class, LoggedIn.class,
				DevMode.class, Role.class, Roles.class);
		AOP.intercept(new TransactionInterceptor(), Transaction.class, Transactional.class);

		Jobs.execute(new Runnable() {
			@Override
			public void run() {
				Log.info("The executor is ready.");
			}
		});
	}

	public static EntityManager createJPAEM(Object[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("pu", U.map());
		EntityManager em = emf.createEntityManager();
		return em;
	}

}
