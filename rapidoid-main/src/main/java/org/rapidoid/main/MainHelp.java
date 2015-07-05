package org.rapidoid.main;

/*
 * #%L
 * rapidoid-main
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("4.0.0")
public class MainHelp {

	public static void processHelp(Object[] args) {
		if (args.length == 1 && args[0].equals("--help")) {
			show("Usage:");
			show("  java -cp <yourapp>.jar com.yourapp.Main [option1 option2 ...]");

			show("\nExample:");
			show("  java -cp <yourapp>.jar com.yourapp.Main port=9090 address=127.0.0.1 cpus=2 workers=4 stateless");

			show("\nAvailable options:");
			opt("mode=(dev|production)", "configure DEV or PRODUCTION mode");
			opt("secret=<SECRET>", "configure app-specific secret token for encryption");
			opt("port=<P>", "listen at port P (default: 8080)");
			opt("address=<ADDR>", "listen at address ADDR (default: 0.0.0.0)");
			opt("stateless", "Run in stateless mode, session becomes cookiepack (default: false)");
			opt("threads=<T>", "start T threads for the job executor service (default: 100)");
			opt("cpus=<C>", "optimize for C number of CPUs (default: the actual number of the CPUs)");
			opt("workers=<W>", "start W number of I/O workers (default: the configured number of CPUs)");
			opt("nodelay", "set the TCP_NODELAY flag to disable Nagle's algorithm (default: false)");
			opt("blockingAccept", "accept connection in BLOCKING mode (default: false)");
			opt("bufSizeKB=<SIZE>", "TCP socket buffer size in KB (default: 16)");

			System.exit(0);
		}
	}

	private static void opt(String opt, String desc) {
		show("  " + opt + U.copyNtimes(" ", 17 - opt.length()) + " - " + desc);
	}

	private static void show(String msg) {
		System.out.println(msg);
	}

}
