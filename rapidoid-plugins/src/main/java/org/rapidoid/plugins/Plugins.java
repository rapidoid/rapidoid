package org.rapidoid.plugins;

import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
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
import org.rapidoid.plugins.templates.DefaultTemplatesPlugin;
import org.rapidoid.plugins.templates.TemplatesPlugin;
import org.rapidoid.plugins.users.DefaultUsersPlugin;
import org.rapidoid.plugins.users.UsersPlugin;
import org.rapidoid.util.U;

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

/*
 Used for code generation:

 Lifecycle|lifecycle
 Languages|languages
 DB|db
 Entities|entities
 Users|users
 Email|email
 SMS|sms
 Cache|cache
 Templates|templates
 */

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public final class Plugins {

	private static final Map<String, Map<String, Plugin>> PLUGINS = U.mapOfMaps();

	private static volatile LifecyclePlugin lifecyclePlugin = new DefaultLifecyclePlugin();
	private static volatile LanguagesPlugin languagesPlugin = new DefaultLanguagesPlugin();
	private static volatile DBPlugin dbPlugin = new DefaultDBPlugin();
	private static volatile EntitiesPlugin entitiesPlugin = new DefaultEntitiesPlugin();
	private static volatile UsersPlugin usersPlugin = new DefaultUsersPlugin();
	private static volatile EmailPlugin emailPlugin = new DefaultEmailPlugin();
	private static volatile SMSPlugin smsPlugin = new DefaultSMSPlugin();
	private static volatile CachePlugin cachePlugin = new DefaultCachePlugin();
	private static volatile TemplatesPlugin templatesPlugin = new DefaultTemplatesPlugin();

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

	public static TemplatesPlugin templates() {
		return templatesPlugin;
	}

	public static void register(Plugin plugin) {
		for (Class<?> interf : Cls.getImplementedInterfaces(plugin.getClass())) {
			if (Plugin.class.isAssignableFrom(interf) && !Plugin.class.equals(interf)) {
				String name = plugin.name();
				Log.info("Registering plugin", "plugin", plugin, "name", name, "type", interf);
				PLUGINS.get(interf).put(name, plugin);
			}
		}

		setPlugin(plugin);
	}

	private static void setPlugin(Plugin plugin) {
		if (plugin instanceof LifecyclePlugin) {
			lifecyclePlugin = (LifecyclePlugin) plugin;
		} else if (plugin instanceof LanguagesPlugin) {
			languagesPlugin = (LanguagesPlugin) plugin;
		} else if (plugin instanceof DBPlugin) {
			dbPlugin = (DBPlugin) plugin;
		} else if (plugin instanceof EntitiesPlugin) {
			entitiesPlugin = (EntitiesPlugin) plugin;
		} else if (plugin instanceof UsersPlugin) {
			usersPlugin = (UsersPlugin) plugin;
		} else if (plugin instanceof EmailPlugin) {
			emailPlugin = (EmailPlugin) plugin;
		} else if (plugin instanceof SMSPlugin) {
			smsPlugin = (SMSPlugin) plugin;
		} else if (plugin instanceof CachePlugin) {
			cachePlugin = (CachePlugin) plugin;
		} else if (plugin instanceof TemplatesPlugin) {
			templatesPlugin = (TemplatesPlugin) plugin;
		} else {
			throw U.notExpected();
		}
	}

	public static LifecyclePlugin lifecycle(String name) {
		return (LifecyclePlugin) PLUGINS.get(LifecyclePlugin.class).get(name);
	}

	public static LanguagesPlugin languages(String name) {
		return (LanguagesPlugin) PLUGINS.get(LanguagesPlugin.class).get(name);
	}

	public static DBPlugin db(String name) {
		return (DBPlugin) PLUGINS.get(DBPlugin.class).get(name);
	}

	public static EntitiesPlugin entities(String name) {
		return (EntitiesPlugin) PLUGINS.get(EntitiesPlugin.class).get(name);
	}

	public static UsersPlugin users(String name) {
		return (UsersPlugin) PLUGINS.get(UsersPlugin.class).get(name);
	}

	public static EmailPlugin email(String name) {
		return (EmailPlugin) PLUGINS.get(EmailPlugin.class).get(name);
	}

	public static SMSPlugin sms(String name) {
		return (SMSPlugin) PLUGINS.get(SMSPlugin.class).get(name);
	}

	public static CachePlugin cache(String name) {
		return (CachePlugin) PLUGINS.get(CachePlugin.class).get(name);
	}

	public static TemplatesPlugin templates(String name) {
		return (TemplatesPlugin) PLUGINS.get(TemplatesPlugin.class).get(name);
	}

}