package org.rapidoid.app;

/*
 * #%L
 * rapidoid-app
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

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Order;
import org.rapidoid.beany.Metadata;
import org.rapidoid.security.annotation.DevMode;
import org.rapidoid.util.Arr;

@Authors("Nikolche Mihajlovski")
public class AppScreens implements Comparator<Class<?>> {

	protected static final String SEARCH_SCREEN = "SearchScreen";

	protected static final String SETTINGS_SCREEN = "SettingsScreen";

	protected static final String ADMIN_SCREEN = "AdminScreen";

	protected static final String[] SPECIAL_SCREENS = { SEARCH_SCREEN, SETTINGS_SCREEN, ADMIN_SCREEN };

	public Class<?>[] constructScreens(Map<String, Class<?>> mainScreens) {

		int screensN = mainScreens.size();
		for (Entry<String, Class<?>> e : mainScreens.entrySet()) {
			Class<?> cls = e.getValue();
			if (isSpecialScreen(cls)) {
				screensN--;
			}
		}

		Class<?>[] screens = new Class[screensN];
		int ind = 0;
		for (Entry<String, Class<?>> e : mainScreens.entrySet()) {
			if (!isSpecialScreen(e.getValue())) {
				screens[ind++] = e.getValue();
			}
		}

		Arrays.sort(screens, this);
		return screens;
	}

	protected boolean isSpecialScreen(Class<?> cls) {
		return Arr.indexOf(SPECIAL_SCREENS, cls.getSimpleName()) >= 0 || Metadata.isAnnotated(cls, DevMode.class);
	}

	@Override
	public int compare(Class<?> o1, Class<?> o2) {
		int cls1 = screenOrder(o1);
		int cls2 = screenOrder(o2);

		return cls1 - cls2;
	}

	protected int screenOrder(Class<?> scrClass) {

		Order order = Metadata.classAnnotation(scrClass, Order.class);
		if (order != null) {
			return order.value();
		}

		String cls = scrClass.getSimpleName();

		if (cls.equals("HomeScreen")) {
			return -1000;
		}

		if (cls.equals("AboutScreen")) {
			return 1000;
		}

		if (cls.equals("HelpScreen")) {
			return 2000;
		}

		return 0;
	}

}
