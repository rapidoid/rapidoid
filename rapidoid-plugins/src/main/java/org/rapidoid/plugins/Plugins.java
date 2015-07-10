package org.rapidoid.plugins;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.plugins.impl.DefaultDBPlugin;
import org.rapidoid.plugins.impl.DefaultEmailPlugin;
import org.rapidoid.plugins.impl.DefaultEntitiesPlugin;
import org.rapidoid.plugins.impl.DefaultLanguagesPlugin;
import org.rapidoid.plugins.impl.DefaultLifecyclePlugin;
import org.rapidoid.plugins.impl.DefaultSMSPlugin;
import org.rapidoid.plugins.impl.DefaultUsersPlugin;
import org.rapidoid.plugins.spec.DBPlugin;
import org.rapidoid.plugins.spec.EmailPlugin;
import org.rapidoid.plugins.spec.EntitiesPlugin;
import org.rapidoid.plugins.spec.LanguagesPlugin;
import org.rapidoid.plugins.spec.LifecyclePlugin;
import org.rapidoid.plugins.spec.SMSPlugin;
import org.rapidoid.plugins.spec.UsersPlugin;

/*
 * #%L
 * rapidoid-plugins
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

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public final class Plugins {

	private static volatile LifecyclePlugin lifecyclePlugin = new DefaultLifecyclePlugin();
	private static volatile LanguagesPlugin languagesPlugin = new DefaultLanguagesPlugin();
	private static volatile DBPlugin dbPlugin = new DefaultDBPlugin();
	private static volatile EntitiesPlugin entitiesPlugin = new DefaultEntitiesPlugin();
	private static volatile UsersPlugin usersPlugin = new DefaultUsersPlugin();
	private static volatile EmailPlugin emailPlugin = new DefaultEmailPlugin();
	private static volatile SMSPlugin smsPlugin = new DefaultSMSPlugin();

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

	static EmailPlugin email() {
		return emailPlugin;
	}

	static SMSPlugin sms() {
		return smsPlugin;
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

	public static void register(EmailPlugin emailPlugin) {
		Log.info("Registering Email plugin", "plugin", emailPlugin);
		Plugins.emailPlugin = emailPlugin;
	}

	public static void register(SMSPlugin smsPlugin) {
		Log.info("Registering SMS plugin", "plugin", smsPlugin);
		Plugins.smsPlugin = smsPlugin;
	}

}
