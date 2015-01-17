package org.rapidoid.demo.taskplanner.gui;

/*
 * #%L
 * rapidoid-demo
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

import org.rapidoid.app.Screen;
import org.rapidoid.db.DB;
import org.rapidoid.security.annotation.Admin;
import org.rapidoid.security.annotation.Manager;
import org.rapidoid.security.annotation.Role;
import org.rapidoid.security.annotation.Roles;
import org.rapidoid.util.Log;
import org.rapidoid.util.U;

@Admin
@Manager
@Roles({ @Role("RESTARTER") })
public class AdminScreen extends Screen {

	public Object[] content = { h2("Manage Application"), cmd("Shutdown") };

	public void onShutdown() {
		DB.shutdown();

		Log.warn("Shutting down the application...");
		U.schedule(new Runnable() {
			@Override
			public void run() {
				Log.warn("Exit application");
				System.exit(0);
			}
		}, 500);
	}

}
