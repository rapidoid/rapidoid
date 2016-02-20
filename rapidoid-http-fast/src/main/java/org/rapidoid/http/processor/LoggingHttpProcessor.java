package org.rapidoid.http.processor;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.log.Log;
import org.rapidoid.net.abstracts.Channel;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class LoggingHttpProcessor extends AbstractHttpProcessor {

	public LoggingHttpProcessor(HttpProcessor next) {
		super(next);
	}

	@Override
	public void request(Channel channel, boolean isGet, boolean isKeepAlive, Range body,
	                    Range verb, Range uri, Range path, Range query, Range protocol, Ranges headers) {

		Buf buf = channel.input();
		Log.info("HTTP request", "verb", buf.get(verb), "uri", buf.get(uri), "protocol", buf.get(protocol));

		next.request(channel, isGet, isKeepAlive, body, verb, uri, path, query, protocol, headers);
	}

}
