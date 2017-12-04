package org.rapidoid.net.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.channels.SocketChannel;

@Authors("Nikolche Mihajlovski")
@Since("5.5.0")
public interface NetWorker {

	void accept(SocketChannel socketChannel) throws IOException;

	void process(RapidoidConnection conn);

	void close(RapidoidConnection conn);

	void wantToWrite(RapidoidConnection conn);

	RapidoidConnection newConnection(boolean client);

	long getMessagesProcessed();

	SSLContext sslContext();

	boolean onSameThread();

	RapidoidHelper helper();

}
