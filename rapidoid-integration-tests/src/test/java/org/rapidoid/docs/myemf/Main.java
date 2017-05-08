package org.rapidoid.docs.myemf;

/*
 * #%L
 * rapidoid-integration-tests
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

import org.rapidoid.setup.My;

import javax.persistence.EntityManagerFactory;

public class Main {

	public static void main(String[] args) {

		/* The EntityManagerFactory's should be properly initialized */

		EntityManagerFactory emf1 = null; // FIXME
		EntityManagerFactory emf2 = null; // FIXME

		My.entityManagerFactoryProvider(req -> {
			return req.path().startsWith("/db1/") ? emf1 : emf2;
		});

	}

}
