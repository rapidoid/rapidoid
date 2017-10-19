package org.rapidoid.config;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.RapidoidInfo;
import org.rapidoid.commons.Str;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.Arrays;
import java.util.List;

/*
 * #%L
 * rapidoid-commons
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
@Since("5.0.2")
public class ConfigHelp extends RapidoidThing {

	public static boolean isHelpRequested(String[] args) {
		return Arrays.equals(args, new String[]{"--help"})
			|| Arrays.equals(args, new String[]{"help"});
	}

	public static void processHelp(String[] args) {
		if (isHelpRequested(args)) {
			showUsage();
			System.exit(0);
		}
	}

	private static void showUsage() {
		show(RapidoidInfo.nameAndInfo());
		show("");
		show("Usage:");

		if (Msc.dockerized()) {
			show("  docker run -it --rm -p <PORT>:8888 [-v <your-app-root>:/app] rapidoid [option1 option2 ...]");
			show("  docker run -d -p <PORT>:8888 [-v <your-app-root>:/app] [-u nobody] rapidoid [option1 option2 ...]");
			show("  docker run -it rapidoid command [arg1 arg2 ...]");
		} else {
			show("  java -cp <yourapp>.jar com.example.Main [option1 option2 ...]");
		}

		show("\nExample:");

		if (Msc.dockerized()) {
			show("  docker run -it --rm --net=host -v $(pwd):/app rapidoid app.services=welcome admin.services=center users.admin.password=my-pass");
			show("  docker run -it --rm -p 80:8888 -v $(pwd):/app -u nobody rapidoid app.services=welcome,ping admin.services=center users.admin.password=my-pass");
			show("  docker run -it --rm rapidoid password");
		} else {
			show("  java -cp <yourapp>.jar com.example.Main on.port=9090 on.address=127.0.0.1 app.services=ping,jmx admin.services=center production users.admin.password=my-pass");
		}

		show("\nMain configuration options:");
		showOpts(ConfigOptions.ALL);

		show("\nSupported commands:");
		showOpts(ConfigOptions.COMMANDS);

		show("\nService activation options:");
		showOpts(ConfigOptions.SERVICES);

		show("\nFor a complete list of options see: http://www.rapidoid.org/the-default-configuration.html");
	}

	private static void showOpts(List<ConfigOption> opts) {
		for (ConfigOption opt : opts) {

			Object def = opt.getDefaultValue();
			String desc = opt.getDesc();

			if (def != null) {
				desc = U.frmt("%s (default: %s)", desc, def);
			}

			opt(opt.getName(), desc);
		}
	}

	private static void opt(String opt, String desc) {
		show("  " + opt + Str.mul(" ", 25 - opt.length()) + " - " + desc);
	}

	private static void show(String msg) {
		U.print(msg);
	}

}
