package org.rapidoid.web;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.http.processor.AbstractHttpProcessor;
import org.rapidoid.http.processor.HttpProcessor;
import org.rapidoid.net.abstracts.Channel;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class AppRestartProcessor extends AbstractHttpProcessor {

	public AppRestartProcessor(Setup setup, HttpProcessor next) {
		super(next);
	}

	@Override
	public void request(Channel channel, boolean isGet, boolean isKeepAlive, Range body,
	                    Range verb, Range uri, Range path, Range query, Range protocol, Ranges headers) {

		Setup.restartIfDirty();

		next.request(channel, isGet, isKeepAlive, body, verb, uri, path, query, protocol, headers);
	}

}
