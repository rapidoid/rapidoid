package org.rapidoid.plugins;

import org.rapidoid.log.Log;
import org.rapidoid.plugins.impl.AbstractDBPlugin;
import org.rapidoid.plugins.impl.AbstractEntitiesPlugin;
import org.rapidoid.plugins.impl.AbstractLanguagesPlugin;
import org.rapidoid.plugins.impl.AbstractLifecyclePlugin;
import org.rapidoid.plugins.impl.AbstractUsersPlugin;
import org.rapidoid.plugins.spec.DBPlugin;
import org.rapidoid.plugins.spec.EntitiesPlugin;
import org.rapidoid.plugins.spec.LanguagesPlugin;
import org.rapidoid.plugins.spec.LifecyclePlugin;
import org.rapidoid.plugins.spec.UsersPlugin;

/*
 * #%L
 * rapidoid-plugins
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

/**
 * @author Nikolche Mihajlovski
 * @since 3.0.0
 */
public final class Plugins {

	private static volatile LifecyclePlugin lifecyclePlugin = new AbstractLifecyclePlugin();
	private static volatile LanguagesPlugin languagesPlugin = new AbstractLanguagesPlugin();
	private static volatile DBPlugin dbPlugin = new AbstractDBPlugin();
	private static volatile EntitiesPlugin entitiesPlugin = new AbstractEntitiesPlugin();
	private static volatile UsersPlugin usersPlugin = new AbstractUsersPlugin();

	static DBPlugin db() {
		return dbPlugin;
	}

	static EntitiesPlugin entities() {
		return entitiesPlugin;
	}

	static LanguagesPlugin languages() {
		return languagesPlugin;
	}

	static UsersPlugin users() {
		return usersPlugin;
	}

	static LifecyclePlugin lifecycle() {
		return lifecyclePlugin;
	}

	public static void register(LifecyclePlugin lifecyclePlugin) {
		Log.info("Registering Lifecycle plugin", "plugin", lifecyclePlugin);
		Plugins.lifecyclePlugin = lifecyclePlugin;
	}

	public static void register(LanguagesPlugin languagesPlugin) {
		Log.info("Registering Languages plugin", "plugin", languagesPlugin);
		Plugins.languagesPlugin = languagesPlugin;
	}

	public static void register(DBPlugin dbPlugin) {
		Log.info("Registering DB plugin", "plugin", dbPlugin);
		Plugins.dbPlugin = dbPlugin;
	}

	public static void register(EntitiesPlugin entitiesPlugin) {
		Log.info("Registering Entities plugin", "plugin", entitiesPlugin);
		Plugins.entitiesPlugin = entitiesPlugin;
	}

	public static void register(UsersPlugin usersPlugin) {
		Log.info("Registering Users plugin", "plugin", usersPlugin);
		Plugins.usersPlugin = usersPlugin;
	}

}
