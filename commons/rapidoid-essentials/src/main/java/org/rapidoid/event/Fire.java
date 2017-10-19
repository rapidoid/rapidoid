package org.rapidoid.event;

import org.rapidoid.RapidoidThing;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.util.Collections;
import java.util.Map;

/*
 * #%L
 * rapidoid-essentials
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

/**
 * @author Nikolche Mihajlovski
 * @since 5.2.0
 */
public class Fire extends RapidoidThing {

	@SuppressWarnings("unchecked")
	public static void event(Event event) {
		EventListener listener = getListenerFor(event);

		if (listener != null) {
			processEvent(event, listener, Collections.EMPTY_MAP);
		}
	}

	public static void event(Event event, String name1, Object value1) {
		EventListener listener = getListenerFor(event);

		if (listener != null) {
			Map<String, Object> data = U.map(name1, value1);
			processEvent(event, listener, data);
		}
	}

	public static void event(Event event, String name1, Object value1, String name2, Object value2) {
		EventListener listener = getListenerFor(event);

		if (listener != null) {
			Map<String, Object> data = U.map(name1, value1, name2, value2);
			processEvent(event, listener, data);
		}
	}

	public static void event(Event event, String name1, Object value1, String name2, Object value2,
	                         String name3, Object value3) {

		EventListener listener = getListenerFor(event);

		if (listener != null) {
			Map<String, Object> data = U.map(name1, value1, name2, value2, name3, value3);
			processEvent(event, listener, data);
		}
	}

	public static void event(Event event, String name1, Object value1, String name2, Object value2,
	                         String name3, Object value3, String name4, Object value4) {

		EventListener listener = getListenerFor(event);

		if (listener != null) {
			Map<String, Object> data = U.map(name1, value1, name2, value2, name3, value3, name4, value4);

			processEvent(event, listener, data);
		}
	}

	public static void event(Event event, String name1, Object value1, String name2, Object value2,
	                         String name3, Object value3, String name4, Object value4, String name5, Object value5) {

		EventListener listener = getListenerFor(event);

		if (listener != null) {
			Map<String, Object> data = U.map(name1, value1, name2, value2, name3, value3, name4, value4, name5, value5);

			processEvent(event, listener, data);
		}
	}

	public static void event(Event event, String name1, Object value1, String name2, Object value2,
	                         String name3, Object value3, String name4, Object value4, String name5, Object value5,
	                         String name6, Object value6) {

		EventListener listener = getListenerFor(event);

		if (listener != null) {
			Map<String, Object> data = U.map(name1, value1, name2, value2, name3, value3, name4, value4, name5, value5,
				name6, value6);

			processEvent(event, listener, data);
		}
	}

	public static void event(Event event, String name1, Object value1, String name2, Object value2,
	                         String name3, Object value3, String name4, Object value4, String name5, Object value5,
	                         String name6, Object value6, String name7, Object value7) {

		EventListener listener = getListenerFor(event);

		if (listener != null) {
			Map<String, Object> data = U.map(name1, value1, name2, value2, name3, value3, name4, value4, name5, value5,
				name6, value6, name7, value7);

			processEvent(event, listener, data);
		}
	}

	public static void event(Event event, String name1, Object value1, String name2, Object value2,
	                         String name3, Object value3, String name4, Object value4, String name5, Object value5,
	                         String name6, Object value6, String name7, Object value7, String name8, Object value8) {

		EventListener listener = getListenerFor(event);

		if (listener != null) {
			Map<String, Object> data = U.map(name1, value1, name2, value2, name3, value3, name4, value4, name5, value5,
				name6, value6, name7, value7, name8, value8);

			processEvent(event, listener, data);
		}
	}

	public static void event(Event event, String name1, Object value1, String name2, Object value2,
	                         String name3, Object value3, String name4, Object value4, String name5, Object value5,
	                         String name6, Object value6, String name7, Object value7, String name8, Object value8,
	                         String name9, Object value9) {

		EventListener listener = getListenerFor(event);

		if (listener != null) {
			Map<String, Object> data = U.map(name1, value1, name2, value2, name3, value3, name4, value4, name5, value5,
				name6, value6, name7, value7, name8, value8, name9, value9);

			processEvent(event, listener, data);
		}
	}

	private static EventListener getListenerFor(Event event) {
		return event.listener();
	}

	private static void processEvent(Event event, EventListener listener, Map<String, Object> data) {
		try {
			listener.onEvent(event, data);

		} catch (Exception e) {
			Log.error("Event listener has thrown an error!", "event", event, "data", data, "listener", listener);
		}
	}

}
