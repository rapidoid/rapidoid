package org.rapidoid.plugins;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.plugins.cache.CachePlugin;
import org.rapidoid.plugins.cache.DefaultCachePlugin;
import org.rapidoid.plugins.db.DBPlugin;
import org.rapidoid.plugins.db.DefaultDBPlugin;
import org.rapidoid.plugins.email.DefaultEmailPlugin;
import org.rapidoid.plugins.email.EmailPlugin;
import org.rapidoid.plugins.entities.DefaultEntitiesPlugin;
import org.rapidoid.plugins.entities.EntitiesPlugin;
import org.rapidoid.plugins.languages.DefaultLanguagesPlugin;
import org.rapidoid.plugins.languages.LanguagesPlugin;
import org.rapidoid.plugins.lifecycle.DefaultLifecyclePlugin;
import org.rapidoid.plugins.lifecycle.LifecyclePlugin;
import org.rapidoid.plugins.sms.DefaultSMSPlugin;
import org.rapidoid.plugins.sms.SMSPlugin;
import org.rapidoid.plugins.users.DefaultUsersPlugin;
import org.rapidoid.plugins.users.UsersPlugin;

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
	private static volatile CachePlugin cachePlugin = new DefaultCachePlugin();

	public static DBPlugin db() {
		return dbPlugin;
	}

	public static EntitiesPlugin entities() {
		return entitiesPlugin;
	}

	public static LanguagesPlugin languages() {
		return languagesPlugin;
	}

	public static UsersPlugin users() {
		return usersPlugin;
	}

	public static LifecyclePlugin lifecycle() {
		return lifecyclePlugin;
	}

	public static EmailPlugin email() {
		return emailPlugin;
	}

	public static SMSPlugin sms() {
		return smsPlugin;
	}

	public static CachePlugin cache() {
		return cachePlugin;
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

	public static void register(CachePlugin cachePlugin) {
		Log.info("Registering Cache plugin", "plugin", cachePlugin);
		Plugins.cachePlugin = cachePlugin;
	}

}
