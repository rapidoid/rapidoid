package org.rapidoid.http;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.bytes.Bytes;
import org.rapidoid.bytes.BytesUtil;
import org.rapidoid.commons.Str;
import org.rapidoid.config.BasicConfig;
import org.rapidoid.config.Conf;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.ctx.UserInfo;
import org.rapidoid.data.BufRanges;
import org.rapidoid.gui.reqinfo.ReqInfo;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.customize.JsonResponseRenderer;
import org.rapidoid.http.impl.MaybeReq;
import org.rapidoid.io.Res;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.u.U;
import org.rapidoid.util.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

/*
 * #%L
 * rapidoid-http-fast
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

@Authors("Nikolche Mihajlovski")
@Since("5.0.0")
public class HttpUtils extends RapidoidThing implements HttpMetadata {

	private static final MediaType DEFAULT_CONTENT_TYPE = MscOpts.hasRapidoidHTML() ? MediaType.HTML_UTF_8 : MediaType.JSON;

	private static final String PAGE_RELOAD = "<h2>&nbsp;Reloading...</h2><script>location.reload();</script>";

	private static final byte[] EMPTY_RESPONSE = {};

	private static final MaybeReq NO_REQ = new MaybeReq() {
		@Override
		public Req getReqOrNull() {
			return null;
		}
	};

	private static volatile Pattern REGEX_VALID_HTTP_RESOURCE = Pattern.compile("(?U)(?:/[\\w\\-\\.]+)*/?");

	private static final Mapper<String[], String> PATH_PARAM_EXTRACTOR = new Mapper<String[], String>() {
		@Override
		public String map(String[] src) throws Exception {
			return src[1].split(":", 2)[0];
		}
	};

	public static Map<String, Serializable> initAndDeserializeToken(Req req) {
		String token = req.cookie(TOKEN, null);

		if (U.isEmpty(token)) {
			token = req.data(TOKEN, null);
		}

		return Tokens.deserialize(token);
	}

	public static void saveTokenBeforeRenderingHeaders(Req req, Map<String, Serializable> tokenData) {
		String token = Tokens.serialize(tokenData);
		setResponseTokenCookie(req.response(), token);
	}

	public static boolean isGetReq(Req req) {
		return req.verb().equals(GET);
	}

	public static boolean isPostReq(Req req) {
		return req.verb().equals(POST);
	}

	public static String resName(Req req) {
		String resName = resName(req.path());
		U.notNull(resName, "resource name");
		return resName;
	}

	public static String resName(String path) {
		if (U.notEmpty(path) && REGEX_VALID_HTTP_RESOURCE.matcher(path).matches() && !path.contains("..")) {
			String res = Str.triml(path, "/");
			return res.isEmpty() ? "index" : Str.trimr(res, ".html");

		} else {
			throw U.rte("Invalid path!");
		}
	}

	public static String viewName(Req req) {
		//return req.route() != null ? inferViewNameFromRoutePath(req.route().path()) : resName(req.path());
		return resName(req.path());
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

	public static Res staticResource(Req req, String... possibleLocations) {
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

	public static String getErrorMessageAndSetCode(Resp resp, Throwable err) {
		ErrCodeAndMsg codeAndMsg = Msc.getErrorCodeAndMsg(err);
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
		BasicConfig defaultConfig = custom.config().sub("gui").or(custom.config().sub("app"));

		if (zone != null) {
			String zoneKey = zone + "-zone";
			return custom.config().sub(zoneKey).or(defaultConfig);

		} else {
			return defaultConfig;
		}
	}

	public static BasicConfig zone(Req req) {
		Customization custom = Customization.of(req);
		return zone(custom, req.zone());
	}

	@SuppressWarnings("unchecked")
	public static Object postprocessResult(Req req, Object result) {

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
			token.remove(Tokens._USER);
			token.remove(Tokens._SCOPE);
		}
	}

	public static TokenAuthData getAuth(Req req) {
		TokenAuthData auth = req.hasToken() ? Tokens.getAuth(req.token()) : null;

		// check if the route is outside of scope
		if (auth != null && U.notEmpty(auth.scope) && !auth.scope.contains(req.verb() + " " + req.path())) auth = null;

		return auth;
	}

	public static String inferRealIpAddress(Req req) {
		// // FIXME if CloudFlare is detected, use req.header("cf-connecting-ip")
		return req.clientIpAddress();
	}

	public static MaybeReq noReq() {
		return NO_REQ;
	}

	public static MaybeReq maybe(Req req) {
		return req != null ? (MaybeReq) req : noReq();
	}

	public static MaybeReq req(Req req) {
		U.notNull(req, "HTTP request");
		return (MaybeReq) req;
	}

	public static int findBodyStart(byte[] response) {
		Bytes bytes = BytesUtil.from(response);
		BufRanges lines = new BufRanges(100);

		int pos = BytesUtil.parseLines(bytes, lines, 0, bytes.limit());
		U.must(pos > 0, "Invalid HTTP response!");

		return pos;
	}

	private static boolean ignoreResponseHeaderInProxy(String name) {
		return name.equalsIgnoreCase("Transfer-encoding")
			|| name.equalsIgnoreCase("Content-length")
			|| name.equalsIgnoreCase("Connection")
			|| name.equalsIgnoreCase("Date")
			|| name.equalsIgnoreCase("Server");
	}

	public static void proxyResponseHeaders(Map<String, String> respHeaders, SimpleHttpResp resp) {
		for (Map.Entry<String, String> hdr : respHeaders.entrySet()) {

			String name = hdr.getKey();
			String value = hdr.getValue();

			if (name.equalsIgnoreCase("Content-type")) {
				resp.contentType = MediaType.of(value);

			} else if (name.equalsIgnoreCase("Set-Cookie")) {

				String[] parts = value.split("=", 2);
				U.must(parts.length == 2, "Invalid value of the Set-Cookie header!");

				if (resp.cookies == null) {
					resp.cookies = U.map();
				}
				resp.cookies.put(parts[0], parts[1]);

			} else if (!ignoreResponseHeaderInProxy(name)) {
				if (resp.headers == null) {
					resp.headers = U.map();
				}
				resp.headers.put(name, value);
			}
		}
	}

	public static MediaType getDefaultContentType() {
		return DEFAULT_CONTENT_TYPE;
	}

	public static void validateViewName(String view) {
		U.must(!view.startsWith("/"), "Invalid view name: '%s'", view);
	}

	public static Map<String, WebData> webParams(Req req) {
		Map<String, WebData> webParams = U.map();

		for (Map.Entry<String, String> e : req.params().entrySet()) {
			webParams.put(e.getKey(), new WebData(e.getValue()));
		}

		return webParams;
	}

}
