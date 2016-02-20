package org.rapidoid.http.processor;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.FastHttpProtocol;
import org.rapidoid.http.HttpMetadata;
import org.rapidoid.net.Server;
import org.rapidoid.net.TCP;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public abstract class AbstractHttpProcessor implements HttpProcessor, HttpMetadata {

	protected final HttpProcessor next;

	protected AbstractHttpProcessor(HttpProcessor next) {
		this.next = next;
	}

	@Override
	public Server listen(String address, int port) {
		FastHttpProtocol protocol = new FastHttpProtocol(this);
		return TCP.server().protocol(protocol).address(address).port(port).build().start();
	}

	@Override
	public Server listen(int port) {
		return listen("0.0.0.0", port);
	}

}
