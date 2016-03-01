package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http-fast
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.MediaType;
import org.rapidoid.commons.Str;
import org.rapidoid.config.Conf;
import org.rapidoid.crypto.Crypto;
import org.rapidoid.data.JSON;
import org.rapidoid.io.Res;
import org.rapidoid.u.U;
import org.rapidoid.util.UTILS;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.Serializable;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.0.0")
public class HttpUtils implements HttpMetadata {

	private static final String PAGE_RELOAD = "<h2>&nbsp;Reloading...</h2><script>location.reload();</script>";

	private static final byte[] EMPTY_RESPONSE = {};

	public static String[] pathSegments(Req req) {
		return Str.triml(req.path(), "/").split("/");
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Serializable> initAndDeserializeCookiePack(Req req) {
		String cookiepack = req.cookie(COOKIEPACK_COOKIE, null);

		if (!U.isEmpty(cookiepack)) {
			byte[] decoded = DatatypeConverter.parseBase64Binary(cookiepack.replace('$', '+'));
			return (Map<String, Serializable>) UTILS.deserialize(Crypto.decrypt(decoded));
		} else {
			return null;
		}
	}

	public static void saveCookipackBeforeRenderingHeaders(Req req, Map<String, Serializable> cookiepack) {
		if (cookiepack != null) {
			byte[] cpack = Crypto.encrypt(UTILS.serialize(cookiepack));
			String encoded = DatatypeConverter.printBase64Binary(cpack).replace('+', '$');
			setCookie(req, COOKIEPACK_COOKIE, encoded, "path=/");
		}
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
				resourceName = Str.sub(resourceName, 0, -5);
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

	public static void setContentTypeForFile(Resp resp, File file) {
		U.must(file.exists());
		setContentType(resp, MediaType.getByFileName(file.getAbsolutePath()));
	}

	public static void setContentTypeForResource(Resp resp, Res resource) {
		U.must(resource.exists());
		setContentType(resp, MediaType.getByFileName(resource.getName()));
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

	public static Res staticResource(String filename, String... possibleLocations) {
		return Res.from(filename, possibleLocations);
	}

	public static Res staticPage(Req req, String... possibleLocations) {
		String resName = resName(req);

		if (hasExtension(resName)) {
			return staticResource(resName, possibleLocations);
		} else {
			Res res = Res.from(resName, possibleLocations);

			if (!res.exists()) {
				res = Res.from(resName + ".html", possibleLocations);
			}

			return res;
		}
	}

	public static String getErrorMessage(Resp resp, Throwable err) {
		Throwable cause = UTILS.rootCause(err, SecurityException.class);

		int code;
		String msg;

		if (cause instanceof SecurityException) {
			code = 403;
			msg = "Access Denied!";
		} else {
			code = 500;
			msg = "Internal Server Error!";
		}

		resp.code(code);
		return U.or(err.getMessage(), msg);
	}

	public static Map<String, ?> jsonError(Resp resp, Throwable error) {
		String errorMessage = getErrorMessage(resp, error);
		return U.map("error", errorMessage, "code", resp.code());
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

			resp.content(Res.from(file).getBytes());
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
			if (resp.code() < 300 || resp.code() >= 400) {
				resp.code(303);
			}

			resp.headers().put(HttpHeaders.LOCATION.name(), redirect);

			if (resp.content() == null && resp.body() == null) {
				resp.body(EMPTY_RESPONSE);
			}
		}
	}

	public static void reload(Req x) {
		Map<String, String> sel = U.map("body", PAGE_RELOAD);
		x.response().json(U.map("_sel_", sel));
	}

	public static String constructUrl(Req x, String path) {
		return (Conf.ROOT.is("https") ? "https://" : "http://") + x.host() + path;
	}

	public static byte[] responseToBytes(Object result, MediaType contentType) {
		if (U.eq(contentType, MediaType.JSON_UTF_8)) {
			return JSON.stringifyToBytes(result);
		} else {
			return UTILS.toBytes(result);
		}
	}

	public static void resultToResponse(Req req, Object result) {
		if (result instanceof Req) {
			if (req != result) {
				req.response().content("Unknown request instance was received as result!");
			}

		} else if (result instanceof Resp) {
			if (req.response() != result) {
				req.response().content("Unknown response instance was received as result!");
			}

		} else {
			req.response().content(result);
		}
	}

}
