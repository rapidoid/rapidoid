package org.rapidoid.http;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Str;
import org.rapidoid.config.BasicConfig;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.crypto.Crypto;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.ctx.UserInfo;
import org.rapidoid.gui.reqinfo.ReqInfo;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.customize.JsonResponseRenderer;
import org.rapidoid.http.impl.PathPattern;
import org.rapidoid.io.Res;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.serialize.Serialize;
import org.rapidoid.u.U;
import org.rapidoid.util.ErrCodeAndMsg;
import org.rapidoid.util.Msc;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.nio.BufferOverflowException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

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

@Authors("Nikolche Mihajlovski")
@Since("5.0.0")
public class HttpUtils extends RapidoidThing implements HttpMetadata {

	private static final String PAGE_RELOAD = "<h2>&nbsp;Reloading...</h2><script>location.reload();</script>";

	private static final byte[] EMPTY_RESPONSE = {};

	public static volatile Pattern REGEX_VALID_HTTP_RESOURCE = Pattern.compile("(?:/[A-Za-z0-9_\\-\\.]+)*/?");

	public static final String _USER = "_user";
	public static final String _EXPIRES = "_expires";

	private static final Mapper<String[], String> PATH_PARAM_EXTRACTOR = new Mapper<String[], String>() {
		@Override
		public String map(String[] src) throws Exception {
			return src[1].split(":", 2)[0];
		}
	};

	@SuppressWarnings("unchecked")
	public static Map<String, Serializable> initAndDeserializeToken(Req req) {
		String token = req.cookie(TOKEN, null);

		if (U.isEmpty(token)) {
			token = req.data(TOKEN, null);
		}

		if (!U.isEmpty(token)) {
			byte[] decoded = Str.fromBase64(token.replace('$', '+').replace('_', '/'));
			byte[] tokenDecrypted = Crypto.decrypt(decoded);
			return (Map<String, Serializable>) Serialize.deserialize(tokenDecrypted);
		} else {
			return null;
		}
	}

	public static void saveTokenBeforeRenderingHeaders(Req req, Map<String, Serializable> tokenData) {
		String token = token(tokenData);
		setResponseTokenCookie(req.response(), token);
	}

	public static String token(Map<String, Serializable> token) {
		if (U.notEmpty(token)) {
			byte[] tokenBytes = serializeToken(token);
			byte[] tokenEncrypted = Crypto.encrypt(tokenBytes);
			return Str.toBase64(tokenEncrypted).replace('+', '$').replace('/', '_');

		} else {
			return "";
		}
	}

	private static byte[] serializeToken(Map<String, Serializable> token) {
		byte[] dest = new byte[2500];

		try {
			int size = Serialize.serialize(dest, token);
			dest = Arrays.copyOf(dest, size);
		} catch (BufferOverflowException e) {
			throw U.rte("The token is too big!");
		}
		return dest;
	}

	public static boolean isGetReq(Req req) {
		return req.verb().equalsIgnoreCase(GET);
	}

	public static boolean isPostReq(Req req) {
		return req.verb().equalsIgnoreCase(POST);
	}

	public static String resName(Req req) {
		return req.route() != null ? resNameFromRoutePath(req.route().path()) : resName(req.path());
	}

	public static String resNameFromRoutePath(String path) {
		path = Str.replace(path, PathPattern.PATH_PARAM_REGEX, PATH_PARAM_EXTRACTOR);
		return resName(path);
	}

	public static String resName(String path) {
		if (U.notEmpty(path) && REGEX_VALID_HTTP_RESOURCE.matcher(path).matches() && !path.contains("..")) {
			String res = Str.triml(path, "/");
			return res.isEmpty() ? "index" : Str.trimr(res, ".html");

		} else {
			return null;
		}
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

	public static Res staticPage(Req req, String... possibleLocations) {
		String resName = resName(req);

		if (resName == null) return null;

		if (hasExtension(resName)) {
			return Res.from(resName, possibleLocations);

		} else {
			Res res = Res.from(resName, possibleLocations);

			if (!res.exists()) {
				res = Res.from(resName + ".html", possibleLocations);
			}

			return res;
		}
	}

	public static ErrCodeAndMsg getErrorCodeAndMsg(Throwable err) {
		Throwable cause = Msc.rootCause(err);

		int code;
		String defaultMsg;
		String msg = cause.getMessage();

		if (cause instanceof SecurityException) {
			code = 403;
			defaultMsg = "Access Denied!";

		} else if (cause instanceof NotFound) {
			code = 404;
			defaultMsg = "The requested resource could not be found!";

		} else if (Msc.isValidationError(cause)) {
			code = 422;
			defaultMsg = "Validation Error!";

			if (cause.getClass().getName().equals("javax.validation.ConstraintViolationException")) {
				Set<ConstraintViolation<?>> violations = ((ConstraintViolationException) cause).getConstraintViolations();

				StringBuilder sb = new StringBuilder();
				sb.append("Validation failed: ");

				for (Iterator<ConstraintViolation<?>> it = U.safe(violations).iterator(); it.hasNext(); ) {
					ConstraintViolation<?> v = it.next();

					sb.append(v.getRootBeanClass().getSimpleName());
					sb.append(".");
					sb.append(v.getPropertyPath());
					sb.append(" (");
					sb.append(v.getMessage());
					sb.append(")");

					if (it.hasNext()) {
						sb.append(", ");
					}
				}

				msg = sb.toString();
			}

		} else {
			code = 500;
			defaultMsg = "Internal Server Error!";
		}

		msg = U.or(msg, defaultMsg);
		return new ErrCodeAndMsg(code, msg);
	}

	public static String getErrorMessageAndSetCode(Resp resp, Throwable err) {
		ErrCodeAndMsg codeAndMsg = getErrorCodeAndMsg(err);
		resp.code(codeAndMsg.code());
		return codeAndMsg.msg();
	}

	public static Map<String, ?> getErrorInfo(Resp resp, Throwable error) {
		String errorMessage = getErrorMessageAndSetCode(resp, error);
		return U.map("error", errorMessage, "code", resp.code(), "status", HttpResponseCodes.status(resp.code()));
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

			resp.result(Res.from(file).getBytes());
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

			if (resp.result() == null && resp.body() == null) {
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

	public static byte[] responseToBytes(Req req, Object result, MediaType contentType, JsonResponseRenderer jsonRenderer) {
		if (U.eq(contentType, MediaType.JSON)) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			try {
				jsonRenderer.renderJson(req, result, out);
			} catch (Exception e) {
				throw U.rte(e);
			}

			return out.toByteArray();
		} else {
			return Msc.toBytes(result);
		}
	}

	public static void resultToResponse(Req req, Object result) {
		if (result instanceof Req) {
			if (req != result) {
				req.response().result("Unknown request instance was received as result!");
			}

		} else if (result instanceof Resp) {
			if (req.response() != result) {
				req.response().result("Unknown response instance was received as result!");
			}

		} else {
			req.response().result(result);
		}
	}

	public static String getContextPath(Req req) {
		return zone(req).entry("contextPath").or("");
	}

	public static BasicConfig zone(Customization custom, String zone) {
		Config appConfig = custom.config().sub("app");

		if (zone != null) {
			String zoneKey = zone + "-zone";
			return custom.config().sub(zoneKey).or(appConfig);

		} else {
			return appConfig;
		}
	}

	public static BasicConfig zone(Req req) {
		Customization custom = Customization.of(req);
		return zone(custom, req.zone());
	}

	@SuppressWarnings("unchecked")
	public static Object postprocessResult(Req req, Object result) throws Exception {

		if (result instanceof Req || result instanceof Resp || result instanceof HttpStatus) {
			return result;

		} else if (result == null) {
			return null; // not found

		} else if ((result instanceof Future<?>) || (result instanceof org.rapidoid.concurrent.Future<?>)) {
			return req.async();

		} else {
			return result;
		}
	}

	public static void setResponseTokenCookie(Resp resp, String token) {
		resp.cookie(TOKEN, token, "HttpOnly");
	}

	public static String cookiePath() {
		String ctxPath = ReqInfo.get().contextPath();
		return U.notEmpty(ctxPath) ? ctxPath : "/";
	}

	public static void clearUserData(Req req) {
		if (Ctxs.hasContext()) {
			Ctxs.required().setUser(UserInfo.ANONYMOUS);
		}

		if (req.hasToken()) {
			Map<String, Serializable> token = req.token();
			token.remove(_USER);
			token.remove(_EXPIRES);
		}
	}

}
