package org.rapidoid.ioc;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Coll;
import org.rapidoid.commons.Deep;
import org.rapidoid.u.U;
import org.rapidoid.util.UTILS;

import java.util.Map;
import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class IoCState {

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

	public IoCState copy() {
		IoCState copy = new IoCState();

		Deep.copy(copy.providedClasses, this.providedClasses, null);
		Deep.copy(copy.providedInstances, this.providedInstances, null);
		Deep.copy(copy.providersByType, this.providersByType, null);
		Deep.copy(copy.instances, this.instances, null);

		return copy;
	}

	public synchronized Map<String, Object> info() {
		return U.map("Provided classes", Deep.copyOf(providedClasses, UTILS.TRANSFORM_TO_SIMPLE_CLASS_NAME),
				"Provided instances", Deep.copyOf(providedInstances, UTILS.TRANSFORM_TO_SIMPLE_CLASS_NAME),
				"Managed instances", Deep.copyOf(instances, UTILS.TRANSFORM_TO_SIMPLE_CLASS_NAME),
				"By type", Deep.copyOf(providersByType, UTILS.TRANSFORM_TO_SIMPLE_CLASS_NAME));
	}

}
