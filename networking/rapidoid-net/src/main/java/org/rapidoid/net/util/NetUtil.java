package org.rapidoid.net.util;

/*
 * #%L
 * rapidoid-net
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.io.IO;
import org.rapidoid.lambda.F3;
import org.rapidoid.net.tls.TLSUtil;
import org.rapidoid.u.U;
import org.rapidoid.util.MscOpts;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

@Authors("Nikolche Mihajlovski")
@Since("5.4.0")
public class NetUtil extends RapidoidThing {

	public static <T> T connect(String address, int port, F3<T, InputStream, BufferedReader, DataOutputStream> protocol) {
		return connect(address, port, 0, protocol);
	}

	public static <T> T connect(String address, int port, int timeout, F3<T, InputStream, BufferedReader, DataOutputStream> protocol) {
		return MscOpts.isTLSEnabled()
			? connectSSL(address, port, timeout, protocol)
			: connectNoSSL(address, port, timeout, protocol);
	}

	private static SSLSocket sslSocket(String address, int port, int timeout) throws Exception {
		SSLContext sc = TLSUtil.createTrustingContext();
		SSLSocketFactory ssf = sc.getSocketFactory();
		SSLSocket socket = (SSLSocket) ssf.createSocket(address, port);
		socket.setSoTimeout(timeout);
		socket.startHandshake();
		return socket;
	}

	private static <T> T connectSSL(String address, int port, int timeout, F3<T, InputStream, BufferedReader, DataOutputStream> protocol) {
		T resp;

		try (SSLSocket socket = sslSocket(address, port, timeout)) {
			socket.setSoTimeout(timeout);

			resp = communicate(protocol, socket);

			socket.close();

		} catch (Exception e) {
			throw U.rte(e);
		}

		return resp;
	}

	private static <T> T connectNoSSL(String address, int port, int timeout, F3<T, InputStream, BufferedReader, DataOutputStream> protocol) {
		T resp;

		try (Socket socket = new Socket(address, port)) {
			socket.setSoTimeout(timeout);

			resp = communicate(protocol, socket);

			socket.close();

		} catch (Exception e) {
			throw U.rte(e);
		}

		return resp;
	}

	private static <T> T communicate(F3<T, InputStream, BufferedReader, DataOutputStream> protocol, Socket socket) throws Exception {

		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		InputStream inputStream = socket.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

		return protocol.execute(inputStream, reader, out);
	}

	public static byte[] writeAndRead(String address, int port, final byte[] req, final int timeout) {
		return connect(address, port, timeout, new F3<byte[], InputStream, BufferedReader, DataOutputStream>() {

			@Override
			public byte[] execute(InputStream in, BufferedReader reader, DataOutputStream out) throws Exception {
				out.write(req);
				return IO.readWithTimeout(in);
			}

		});
	}

}
