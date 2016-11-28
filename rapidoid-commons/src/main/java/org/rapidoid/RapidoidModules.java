package org.rapidoid;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import java.util.ServiceLoader;
import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class RapidoidModules extends RapidoidThing {

	public static Set<RapidoidModule> all() {
		ServiceLoader<RapidoidModule> serviceLoader = ServiceLoader.load(RapidoidModule.class);
		return U.set(serviceLoader);
	}

}
