package org.rapidoid.pojo;

/*
 * #%L
 * rapidoid-pojo
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import org.rapidoid.pojo.impl.PojoDispatcherImpl;
import org.rapidoid.util.U;

public class POJO {

	public static final String SERVICE_SUFFIX = "Service";

	public static List<Class<?>> scanServices() {
		return U.classpathClasses("*", ".+" + SERVICE_SUFFIX, null);
	}

	public static PojoDispatcher dispatcher(Object... services) {
		return new PojoDispatcherImpl(services);
	}

	public static PojoDispatcher dispatcher(Class<?>... serviceClasses) {
		Object[] services = new Object[serviceClasses.length];

		for (int i = 0; i < services.length; i++) {
			try {
				services[i] = serviceClasses[i].newInstance();
			} catch (Exception e) {
				throw U.rte(e);
			}
		}

		return dispatcher(services);
	}

}
