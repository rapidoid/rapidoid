package org.rapidoid.pojo.impl;

import java.util.Map;

import org.rapidoid.pojo.PojoRequest;

public class PojoRequestImpl implements PojoRequest {

	private final String command;
	private final String uri;
	private final Map<String, String> extra;

	public PojoRequestImpl(String command, String uri, Map<String, String> extra) {
		this.command = command;
		this.uri = uri;
		this.extra = extra;
	}

	@Override
	public String command() {
		return command;
	}

	@Override
	public String path() {
		return uri;
	}

	@Override
	public Map<String, String> params() {
		return extra;
	}

	@Override
	public String toString() {
		return "PojoRequestImpl [command=" + command + ", uri=" + uri + ", extra=" + extra + "]";
	}

}
