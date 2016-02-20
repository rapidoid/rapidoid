package org.rapidoid.http.processor;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.net.abstracts.Channel;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class NotFoundHttpProcessor extends AbstractHttpProcessor {

	private static final byte[] HTTP_404_NOT_FOUND = "HTTP/1.1 404 Not Found\r\nContent-Length: 10\r\n\r\nNot found!"
			.getBytes();

	public NotFoundHttpProcessor() {
		super(null);
	}

	@Override
	public void request(Channel channel, boolean isGet, boolean isKeepAlive, Range body,
	                    Range verb, Range uri, Range path, Range query, Range protocol, Ranges headers) {

		channel.write(HTTP_404_NOT_FOUND);
		channel.done();
		channel.closeIf(!isKeepAlive);
	}

}
