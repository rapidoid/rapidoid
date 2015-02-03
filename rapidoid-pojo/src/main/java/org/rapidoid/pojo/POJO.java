package org.rapidoid.pojo;

/*
 * #%L
 * rapidoid-pojo
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.pojo.impl.PojoDispatcherImpl;
import org.rapidoid.util.Cls;
import org.rapidoid.util.Scan;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
public class POJO {

	public static final String SERVICE_SUFFIX = "Service";

	public static List<Class<?>> scanServices() {
		return Scan.bySuffix(SERVICE_SUFFIX, null, null);
	}

	public static PojoDispatcher dispatcher(Class<?>... serviceClasses) {
		return new PojoDispatcherImpl(Cls.classMap(U.list(serviceClasses)));
	}

	public static PojoDispatcher serviceDispatcher() {
		List<Class<?>> services = scanServices();
		return dispatcher(services.toArray(new Class[services.size()]));
	}

}
