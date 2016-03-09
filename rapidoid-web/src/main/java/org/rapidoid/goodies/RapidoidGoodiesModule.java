package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.setup.Admin;
import org.rapidoid.setup.Dev;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class RapidoidGoodiesModule {

	static {
		if (Dev.setup().withGoodies()) {
			Log.info("Activating Dev goodies");

			Dev.page("/").render(new RoutesHandler());
			Dev.page("/config").render(new ConfigHandler());
		}

		if (Admin.setup().withGoodies()) {
			Log.info("Activating Admin goodies");

			Admin.page("/").render(Goodies.graphs());
			Admin.page("/routes").render(new RoutesHandler());

			Admin.page("/jmx/memory").render(Goodies.memory());
			Admin.page("/jmx/mempool").render(Goodies.memoryPool());
			Admin.page("/jmx/classes").render(Goodies.classes());
			Admin.page("/jmx/os").render(Goodies.os());
			Admin.page("/jmx/threads").render(Goodies.threads());
			Admin.page("/jmx/compilation").render(Goodies.compilation());
			Admin.page("/jmx/runtime").render(Goodies.runtime());
			Admin.page("/jmx/gc").render(Goodies.gc());
		}
	}

}