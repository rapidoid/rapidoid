package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.Btn;
import org.rapidoid.gui.GUI;
import org.rapidoid.job.Jobs;
import org.rapidoid.setup.Setup;
import org.rapidoid.u.U;

import java.util.concurrent.Callable;

/*
 * #%L
 * rapidoid-web
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
public class TerminateHandler extends GUI implements Callable<Object> {

	@Override
	public Object call() throws Exception {

		Btn shutdown = btn("Shutdown").danger()
			.confirm("Do you really want to SHUTDOWN / RESTART the application?")
			.onClick(new Runnable() {
				         @Override
				         public void run() {
					         shutdownSoon();
				         }
			         }
			);

		Btn halt = btn("Halt").danger()
			.confirm("Do you really want to HALT / RESTART the application?")
			.onClick(new Runnable() {
				         @Override
				         public void run() {
					         haltSoon();
				         }
			         }
			);

		return multi(h1("Terminate / restart the application?"), shutdown, halt);
	}

	public static void shutdownSoon() {
		new Thread() {
			@Override
			public void run() {
				U.sleep(1000);
				Jobs.shutdown();
				Setup.shutdownAll();
				System.exit(0);
			}
		}.start();
	}

	public static void haltSoon() {
		new Thread() {
			@Override
			public void run() {
				U.sleep(1000);
				Jobs.shutdownNow();
				Setup.haltAll();
				System.exit(0);
			}
		}.start();
	}
}

