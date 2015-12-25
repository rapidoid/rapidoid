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
import org.rapidoid.commons.MediaType;
import org.rapidoid.config.Conf;
import org.rapidoid.data.JSON;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;
import org.rapidoid.io.Res;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("5.0.0")
public class HttpUtils implements HttpMetadata {

	private static final String PAGE_RELOAD = "<h2>&nbsp;Reloading...</h2><script>location.reload();</script>";

	private static final byte[] EMPTY_RESPONSE = {};

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

	public static String[] pathSegments(Req req) {
		return U.triml(req.path(), "/").split("/");
	}

	public static Map<String, Serializable> initAndDeserializeCookiePack(Req req) {
		String cookiepack = req.cookie(COOKIEPACK_COOKIE, null);

		if (!U.isEmpty(cookiepack) && !cookiepack.equals("null")) {

			String cpackJsonStr = '"' + cookiepack + '"';
			byte[] cpbytes = JSON.parseBytes(cpackJsonStr);
			return deserializeCookiepack(cpbytes);

		} else {
			return null;
		}
	}

	public static void saveCookipackBeforeClosingHeaders(Req req, Map<String, Serializable> cookiepack) {
		byte[] cpack = serializeCookiepack(cookiepack);

		if (cpack != null) {
			String json = U.mid(JSON.stringify(cpack), 1, -1);
			setCookie(req, COOKIEPACK_COOKIE, json, "path=/");
		}
	}

	public Map<String, Serializable> loadState(Req req) {
		if (isPostReq(req)) {
			String pageLocals = req.posted(VIEWSTATE, null);

			if (!U.isEmpty(pageLocals) && !pageLocals.equals("null")) {
				byte[] bytes = JSON.parseBytes('"' + pageLocals + '"');
				return deserializeLocals(bytes);
			}
		}

		return null;
	}

	public static boolean isGetReq(Req req) {
		return req.verb().equalsIgnoreCase(GET);
	}

	public static boolean isPostReq(Req req) {
		return req.verb().equalsIgnoreCase(POST);
	}

	public static String resName(Req req) {
		String resourceName = req.path().substring(1);

		if (resourceName.isEmpty()) {
			resourceName = "index";
		} else {
			if (resourceName.endsWith(".html")) {
				resourceName = U.mid(resourceName, 0, -5);
			}
		}

		return resourceName;
	}

	public static String verbAndResourceName(Req req) {
		return req.verb().toUpperCase() + "/" + resName(req);
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

	public static void setContentTypeForFile(Resp resp, File file) {
		U.must(file.exists());
		setContentType(resp, MediaType.getByFileName(file.getAbsolutePath()));
	}

	public static void setContentTypeForResource(Resp resp, Res resource) {
		U.must(resource.exists());
		setContentType(resp, MediaType.getByFileName(resource.getShortName()));
	}

	public static void setContentType(Resp resp, MediaType mediaType) {
		resp.contentType(mediaType);
	}

	public static void setCookie(Req req, String name, String value, String... extras) {
		value = cookieValueWithExtras(value, extras);
		req.response().cookies().put(name, value);
	}

	public static String cookieValueWithExtras(String value, String... extras) {
		if (extras.length > 0) {
			value += "; " + U.join("; ", extras);
		}

		return value;
	}

	public static Res staticResource(String filename) {
		String firstFile = Conf.staticPath() + "/" + filename;
		String defaultFile = Conf.staticPathDefault() + "/" + filename;
		return Res.from(filename, true, firstFile, defaultFile);
	}

	public static Res staticPage(Req req) {
		String resName = resName(req);

		if (hasExtension(resName)) {
			return staticResource(resName);
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

	public static void postProcessResponse(Resp resp) {
		postProcessRedirect(resp);
		postProcessFile(resp);
		postProcessFilename(resp);
	}

	private static void postProcessFile(Resp resp) {
		File file = resp.file();
		if (file != null) {
			U.must(file.exists());

			if (resp.filename() == null) {
				resp.filename();
			}

			setContentTypeForFile(resp, file);

			resp.content(Res.from(file.getAbsolutePath()).getBytes());
		}
	}

	private static void postProcessFilename(Resp resp) {
		String filename = resp.filename();
		if (filename != null) {
			resp.headers().put(HttpHeaders.CONTENT_DISPOSITION.name(), "attachment; filename=\"" + filename + "\"");
			resp.headers().put(HttpHeaders.CACHE_CONTROL.name(), "private");
		}
	}

	private static void postProcessRedirect(Resp resp) {
		String redirect = resp.redirect();
		if (redirect != null) {
			resp.code(303);
			resp.headers().put(HttpHeaders.LOCATION.name(), redirect);
			if (resp.content() == null && resp.body() == null) {
				resp.body(EMPTY_RESPONSE);
			}
		}
	}

	public static boolean isDevMode(Req x) {
		return Conf.dev();
	}

	public static final void reload(Req x) {
		Map<String, String> sel = U.map("body", PAGE_RELOAD);
		x.response().json(U.map("_sel_", sel));
	}

	public static String constructUrl(Req x, String path) {
		return (Conf.is("https") ? "https://" : "http://") + x.host() + path;
	}

}
