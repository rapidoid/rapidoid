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
import org.rapidoid.aop.AOP;
import org.rapidoid.app.Apps;
import org.rapidoid.app.TransactionInterceptor;
import org.rapidoid.appctx.AppClasspathEntitiesPlugin;
import org.rapidoid.appctx.Application;
import org.rapidoid.appctx.Applications;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.job.Jobs;
import org.rapidoid.log.Log;
import org.rapidoid.plugins.Plugins;
import org.rapidoid.plugins.db.hibernate.HibernateDBPlugin;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class Quick {

	public static void run(Application app, String[] args, Object... config) {
		bootstrap(app, args, config);
		serve(app, args, config);
	}

	public static void serve(Application app, String[] args, Object... config) {
		Apps.serve(app, args, config);
	}

	public static void bootstrap(Application app, final String[] args, Object... config) {
		Apps.bootstrap(app, args, config);

		Applications.main().setDefaultApp(app);
		Applications.main().register(app);

		Ctxs.open();
		Ctxs.setPersisterProvider(new QuickJPA(config));

		Plugins.register(new HibernateDBPlugin());
		Plugins.register(new AppClasspathEntitiesPlugin());

		// TODO provide better support for javax.transaction.Transactional
		AOP.register(Transactional.class, new TransactionInterceptor());

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
