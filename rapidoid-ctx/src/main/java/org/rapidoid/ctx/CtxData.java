package org.rapidoid.ctx;

/*
 * #%L
 * rapidoid-ctx
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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("2.5.0")
public class CtxData {

	private String username = null;

	private Set<String> roles = null;

	private Object app = null;

	private org.rapidoid.ctx.JobStatusListener listener = null;

	private Object persister = null;

	private String host = null;

	private String uri = null;

	private String verb = null;

	private Map<String, Object> data = null;

	private Map<String, java.io.Serializable> session = null;

	private Map<String, Object> extras = null;

	public synchronized CtxData username(String username) {
		this.username = username;
		return this;
	}

	public synchronized String username() {
		return this.username;
	}

	public synchronized CtxData roles(Set<String> roles) {
		this.roles = roles;
		return this;
	}

	public synchronized Set<String> roles() {
		return this.roles;
	}

	public synchronized CtxData app(Object app) {
		this.app = app;
		return this;
	}

	public synchronized Object app() {
		return this.app;
	}

	public synchronized CtxData listener(org.rapidoid.ctx.JobStatusListener listener) {
		this.listener = listener;
		return this;
	}

	public synchronized org.rapidoid.ctx.JobStatusListener listener() {
		return this.listener;
	}

	public synchronized CtxData persister(Object persister) {
		this.persister = persister;
		return this;
	}

	public synchronized Object persister() {
		return this.persister;
	}

	public synchronized CtxData host(String host) {
		this.host = host;
		return this;
	}

	public synchronized String host() {
		return this.host;
	}

	public synchronized CtxData uri(String uri) {
		this.uri = uri;
		return this;
	}

	public synchronized String uri() {
		return this.uri;
	}

	public synchronized CtxData verb(String verb) {
		this.verb = verb;
		return this;
	}

	public synchronized String verb() {
		return this.verb;
	}

	public synchronized CtxData data(Map<String, Object> data) {
		this.data = data;
		return this;
	}

	public synchronized Map<String, Object> data() {
		return this.data;
	}

	public synchronized CtxData session(Map<String, java.io.Serializable> session) {
		this.session = session;
		return this;
	}

	public synchronized Map<String, java.io.Serializable> session() {
		return this.session;
	}

	public synchronized CtxData extras(Map<String, Object> extras) {
		this.extras = extras;
		return this;
	}

	public synchronized Map<String, Object> extras() {
		return this.extras;
	}

	public synchronized <T> T call(Callable<T> action) {
		return Ctx.executeInCtx(this, action);
	}

}