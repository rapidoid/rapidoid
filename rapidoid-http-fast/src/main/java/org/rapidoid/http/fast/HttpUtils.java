package org.rapidoid.http.fast;

/*
 * #%L
 * rapidoid-http-fast
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

import java.io.File;
import java.io.Serializable;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.data.JSON;
import org.rapidoid.io.Res;
import org.rapidoid.log.Log;
import org.rapidoid.mime.MediaType;
import org.rapidoid.u.U;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("5.0.0")
public class HttpUtils implements HttpMetadata {

	public static String path(Map<String, Object> state) {
		return (String) U.get(state, PATH);
	}

	public static String verb(Map<String, Object> state) {
		return (String) U.get(state, VERB);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> headers(Map<String, Object> state) {
		return (Map<String, String>) state.get(HEADERS);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> cookies(Map<String, Object> state) {
		return (Map<String, String>) state.get(COOKIES);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> setHeaders(Map<String, Object> state) {
		return (Map<String, String>) state.get(SET_HEADERS);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> setCookies(Map<String, Object> state) {
		return (Map<String, String>) state.get(SET_COOKIES);
	}

	public static String getSessionId(Map<String, Object> state) {
		return cookies(state).get(SESSION_COOKIE);
	}

	public static byte[] serializeLocals(Map<String, Serializable> locals) {
		return locals != null ? UTILS.serialize(locals) : null;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Serializable> deserializeLocals(byte[] bytes) {
		return (Map<String, Serializable>) UTILS.deserialize(bytes);
	}

	public static byte[] serializeCookiepack(Map<String, Serializable> cookiepack) {
		return cookiepack != null ? UTILS.serialize(cookiepack) : null;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Serializable> deserializeCookiepack(byte[] bytes) {
		return (Map<String, Serializable>) UTILS.deserialize(bytes);
	}

	public static String[] pathSegments(Map<String, Object> state) {
		String path = path(state);
		return U.triml(path, "/").split("/");
	}

	public static Map<String, Serializable> initAndDeserializeCookiePack(Map<String, Object> state) {
		String cookiepack = U.get(cookies(state), COOKIEPACK_COOKIE, null);

		if (!U.isEmpty(cookiepack) && !cookiepack.equals("null")) {

			String cpackJsonStr = '"' + cookiepack + '"';
			byte[] cpbytes = JSON.parseBytes(cpackJsonStr);
			return deserializeCookiepack(cpbytes);

		} else {
			return null;
		}
	}

	public static void saveCookipackBeforeClosingHeaders(Map<String, Object> state, Map<String, Serializable> cookiepack) {
		byte[] cpack = serializeCookiepack(cookiepack);

		if (cpack != null) {
			String json = U.mid(JSON.stringify(cpack), 1, -1);
			setCookie(state, COOKIEPACK_COOKIE, json, "path=/");
		}
	}

	public Map<String, Serializable> loadState(Map<String, Object> state) {
		if (isPostReq(state)) {
			String pageLocals = (String) state.get(VIEWSTATE);

			if (!U.isEmpty(pageLocals) && !pageLocals.equals("null")) {
				byte[] bytes = JSON.parseBytes('"' + pageLocals + '"');
				return deserializeLocals(bytes);
			}
		}

		return null;
	}

	public static boolean isGetReq(Map<String, Object> state) {
		return verb(state).equalsIgnoreCase("GET");
	}

	public static boolean isPostReq(Map<String, Object> state) {
		return verb(state).equalsIgnoreCase("POST");
	}

	public static String resName(Map<String, Object> state) {
		String resourceName = path(state).substring(1);

		if (resourceName.isEmpty()) {
			resourceName = "index";
		} else {
			if (resourceName.endsWith(".html")) {
				resourceName = U.mid(resourceName, 0, -5);
			}
		}

		return resourceName;
	}

	public static String verbAndResourceName(Map<String, Object> state) {
		return verb(state).toUpperCase() + "/" + resName(state);
	}

	public static boolean hasExtension(String name) {
		int pos = name.lastIndexOf('.');
		return pos > 0 && pos < name.length() - 1;
	}

	public static String renderState(Map<String, Serializable> pageLocals) {
		try {
			return JSON.stringify(serializeLocals(pageLocals));
		} catch (Exception e) {
			Log.error("Cannot render the local page state!", e);
			return "{}";
		}
	}

	public static void redirect(Map<String, Object> state, String url) {
		setResponseCode(state, 303);
		setHeader(state, HttpHeaders.LOCATION.name(), url);
	}

	public static void setContentTypeForFile(Map<String, Object> state, File file) {
		U.must(file.exists());
		setContentType(state, MediaType.getByFileName(file.getAbsolutePath()));
	}

	public static void setContentTypeForResource(Map<String, Object> state, Res resource) {
		U.must(resource.exists());
		setContentType(state, MediaType.getByFileName(resource.getShortName()));
	}

	public static void setContentType(Map<String, Object> state, MediaType mediaType) {
		setContentType(state, mediaType.getBytes());
	}

	public static void setHeader(Map<String, Object> state, String name, String value) {
		setHeaders(state).put(name, value);
	}

	public static void setCookie(Map<String, Object> state, String name, String value, String... extras) {
		value = cookieValueWithExtras(value, extras);
		setCookies(state).put(name, value);
	}

	public static String cookieValueWithExtras(String value, String... extras) {
		if (extras.length > 0) {
			value += "; " + U.join("; ", extras);
		}

		return value;
	}

	public static void setResponseCode(Map<String, Object> state, int responseCode) {
		state.put(CODE, responseCode);
	}

	public static void setContentType(Map<String, Object> state, byte[] contentType) {
		state.put(CONTENT_TYPE, contentType);
	}

	public static Res staticResource(String filename) {
		String firstFile = Conf.staticPath() + "/" + filename;
		String defaultFile = Conf.staticPathDefault() + "/" + filename;
		return Res.from(filename, true, firstFile, defaultFile);
	}

	public static Res staticPage(Map<String, Object> state) {
		String resName = HttpUtils.resName(state);

		if (HttpUtils.hasExtension(resName)) {
			return HttpUtils.staticResource(resName);
		} else {
			String resNameDotHtml = resName + ".html";

			String firstFile = Conf.staticPath() + "/" + resName;
			String defaultFile = Conf.staticPathDefault() + "/" + resName;

			String firstHtmlFile = Conf.staticPath() + "/" + resNameDotHtml;
			String defaultHtmlFile = Conf.staticPathDefault() + "/" + resNameDotHtml;

			return Res.from(resName, true, firstFile, defaultFile, firstHtmlFile, defaultHtmlFile);
		}
	}

	public static String getErrorMessage(Throwable err) {
		Throwable cause = UTILS.rootCause(err);

		String details = err.getClass().getSimpleName() + " (" + U.safe(err.getMessage()) + ")";

		if (cause instanceof SecurityException) {
			return U.frmt("Access Denied: %s", details);
		} else {
			return U.frmt("Internal Server Error: %s", details);
		}
	}

}
