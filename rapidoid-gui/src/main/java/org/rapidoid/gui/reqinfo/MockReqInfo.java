package org.rapidoid.gui.reqinfo;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HttpVerb;
import org.rapidoid.io.Upload;
import org.rapidoid.u.U;

import java.io.Serializable;
import java.util.*;

/*
 * #%L
 * rapidoid-gui
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
@Since("5.0.4")
public class MockReqInfo extends AbstractReqInfo {

	private String verb = "GET";

	private String path = "/";

	private String uri = "/";

	private String host = "localhost";

	private String zone = "main";

	private String contextPath = "/";

	private Map<String, Object> data = U.map();

	private Map<String, String> params = U.map();

	private Map<String, Object> posted = U.map();

	private Map<String, List<Upload>> files = U.map();

	private Map<String, String> headers = U.map();

	private Map<String, String> cookies = U.map();

	private Map<String, Object> attributes = U.map();

	private Map<String, Serializable> token = U.map();

	private String username;

	private Set<String> roles = U.set();

	public static MockReqInfo set(String verbAndUri) {
		MockReqInfo mock = new MockReqInfo();

		String[] parts = verbAndUri.split(" ");
		String[] uriParts = parts[1].split("\\?");

		mock.setVerb(parts[0]);
		mock.setUri(parts[1]);
		mock.setPath(uriParts[0]);

		ReqInfo.INFO = mock;
		return mock;
	}

	@Override
	public String verb() {
		return verb;
	}

	public MockReqInfo setVerb(String verb) {
		this.verb = verb;
		return this;
	}

	@Override
	public String path() {
		return path;
	}

	public MockReqInfo setPath(String path) {
		this.path = path;
		return this;
	}

	@Override
	public String uri() {
		return uri;
	}

	public MockReqInfo setUri(String uri) {
		this.uri = uri;
		return this;
	}

	@Override
	public String host() {
		return host;
	}

	public MockReqInfo setHost(String host) {
		this.host = host;
		return this;
	}

	@Override
	public Map<String, Object> data() {
		return data;
	}

	public MockReqInfo setData(Map<String, Object> data) {
		this.data = data;
		return this;
	}

	@Override
	public Map<String, String> params() {
		return params;
	}

	public MockReqInfo setParams(Map<String, String> params) {
		this.params = params;
		return this;
	}

	@Override
	public Map<String, Object> posted() {
		return posted;
	}

	public MockReqInfo setPosted(Map<String, Object> posted) {
		this.posted = posted;
		return this;
	}

	@Override
	public Map<String, List<Upload>> files() {
		return files;
	}

	public MockReqInfo setFiles(Map<String, List<Upload>> files) {
		this.files = files;
		return this;
	}

	@Override
	public Map<String, String> headers() {
		return headers;
	}

	public MockReqInfo setHeaders(Map<String, String> headers) {
		this.headers = headers;
		return this;
	}

	@Override
	public Map<String, String> cookies() {
		return cookies;
	}

	public MockReqInfo setCookies(Map<String, String> cookies) {
		this.cookies = cookies;
		return this;
	}

	@Override
	public Map<String, Object> attrs() {
		return attributes;
	}

	public MockReqInfo setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
		return this;
	}

	@Override
	public Map<String, Serializable> token() {
		return token;
	}

	public MockReqInfo setToken(Map<String, Serializable> token) {
		this.token = token;
		return this;
	}

	@Override
	public String username() {
		return username;
	}

	public MockReqInfo setUsername(String username) {
		this.username = username;
		return this;
	}

	@Override
	public Set<String> roles() {
		return roles;
	}

	public MockReqInfo setRoles(Set<String> roles) {
		this.roles = roles;
		return this;
	}

	@Override
	public String zone() {
		return zone;
	}

	public MockReqInfo zone(String zone) {
		this.zone = zone;
		return this;
	}

	@Override
	public String contextPath() {
		return contextPath;
	}

	@Override
	public boolean hasRoute(HttpVerb verb, String uri) {
		return false;
	}

	@Override
	public String view() {
		return null;
	}

	@Override
	public void setHeader(String name, String value) {
	}

	public MockReqInfo contextPath(String contextPath) {
		this.contextPath = contextPath;
		return this;
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		return "MockReqInfo [verb=" + verb
			+ ", path=" + path
			+ ", uri=" + uri
			+ ", host=" + host
			+ ", zone=" + zone
			+ ", contextPath=" + contextPath
			+ ", data=" + (data != null ? toString(data.entrySet(), maxLen) : null)
			+ ", params=" + (params != null ? toString(params.entrySet(), maxLen) : null)
			+ ", posted=" + (posted != null ? toString(posted.entrySet(), maxLen) : null)
			+ ", files=" + (files != null ? toString(files.entrySet(), maxLen) : null)
			+ ", headers=" + (headers != null ? toString(headers.entrySet(), maxLen) : null)
			+ ", cookies=" + (cookies != null ? toString(cookies.entrySet(), maxLen) : null)
			+ ", attributes=" + (attributes != null ? toString(attributes.entrySet(), maxLen) : null)
			+ ", username=" + username
			+ ", roles=" + (roles != null ? toString(roles, maxLen) : null) + "]";
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}

	@Override
	public boolean exists() {
		return true;
	}

}
