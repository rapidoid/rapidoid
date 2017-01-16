package org.rapidoid.ioc.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.commons.Deep;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.Map;
import java.util.Set;

/*
 * #%L
 * rapidoid-inject
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class IoCState extends RapidoidThing {

	final Set<Class<?>> providedClasses = U.set();

	final Set<Object> providedInstances = U.set();

	final Map<Class<?>, Set<Object>> providersByType = Coll.mapOfSets();

	final Set<Object> instances = U.set();

	public synchronized void reset() {
		providedClasses.clear();
		providedInstances.clear();
		instances.clear();
		providersByType.clear();
	}

	public synchronized IoCState copy() {
		IoCState copy = new IoCState();

		Deep.copy(copy.providedClasses, this.providedClasses, null);
		Deep.copy(copy.providedInstances, this.providedInstances, null);
		Deep.copy(copy.providersByType, this.providersByType, null);
		Deep.copy(copy.instances, this.instances, null);

		return copy;
	}

	public synchronized Map<String, Object> info() {
		return U.map("Provided classes", Deep.copyOf(providedClasses, Msc.TRANSFORM_TO_SIMPLE_CLASS_NAME),
			"Provided instances", Deep.copyOf(providedInstances, Msc.TRANSFORM_TO_SIMPLE_CLASS_NAME),
			"Managed instances", Deep.copyOf(instances, Msc.TRANSFORM_TO_SIMPLE_CLASS_NAME),
			"By type", Deep.copyOf(providersByType, Msc.TRANSFORM_TO_SIMPLE_CLASS_NAME));
	}

	public synchronized boolean isEmpty() {
		return providedClasses.isEmpty() && providedInstances.isEmpty() && providersByType.isEmpty() && instances.isEmpty();
	}

}
