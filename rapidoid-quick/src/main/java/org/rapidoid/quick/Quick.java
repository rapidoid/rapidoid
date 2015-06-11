package org.rapidoid.quick;

/*
 * #%L
 * rapidoid-quick
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.app.Apps;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.jpa.dbplugin.JPADBPlugin;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class Quick {

	public static void main(String[] args) {
		run(args);
	}

	public static void run(String[] args) {
		run((Object[]) args);
	}

	public static void run(Object... args) {
		bootstrap(args);
		serve(args);
	}

	public static void serve(Object... args) {
		Apps.serve(args);
	}

	public static void bootstrap(Object... args) {
		Ctx.setPersistorFactory(new QuickJPA(args));
		JPADBPlugin db = new JPADBPlugin();

		List<Object> appArgs = U.<Object> list(db);
		appArgs.addAll(U.list(args));
		Apps.bootstrap(U.array(appArgs));

		// eager JPA initialization
		QuickJPA.createEM(args);
	}

	public static EntityManager createJPAEM(Object[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("pu-main-h2", U.map());
		EntityManager em = emf.createEntityManager();
		return em;
	}

}
