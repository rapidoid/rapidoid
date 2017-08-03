package org.rapidoid.net.tls;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

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

@Authors("Nikolche Mihajlovski")
@Since("5.4.0")
public class TLSUtil extends RapidoidThing {

	public static SSLContext createTrustingContext() {
		SSLContext sslContext;

		try {
			sslContext = SSLContext.getInstance("TLS");
		} catch (NoSuchAlgorithmException e) {
			throw U.rte(e);
		}

		TrustManager tm = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};

		try {
			sslContext.init(null, new TrustManager[]{tm}, null);
		} catch (KeyManagementException e) {
			throw U.rte(e);
		}

		return sslContext;
	}

	public static SSLContext createContext(String keystore, char[] keystorePassword, char[] keyManagerPassword,
	                                       String truststore, char[] truststorePassword, boolean selfSignedTLS) {

		U.must(U.notEmpty(keystore), "The TLS keystore filename isn't configured!");

		boolean keystoreExists = new File(keystore).exists();

		U.must(keystoreExists || selfSignedTLS,
			"The keystore '%s' doesn't exist and self-signed certificate generation is disabled!", keystore);

		try {
			if (!keystoreExists && selfSignedTLS) {
				SelfSignedCertInfo info = new SelfSignedCertInfo();

				info.alias("rapidoid");
				info.password(keystorePassword);

				Log.warn("Keystore doesn't exist, creating a keystore with self-signed certificate",
					"keystore", keystore, "alias", info.alias());

				SelfSignedCertGen.generate(info, keystore, keystorePassword);
			}

			Log.info("Initializing TLS context", "keystore", keystore, "truststore", truststore);

			KeyManager[] keyManagers = initKeyManagers(keystore, keystorePassword, keyManagerPassword);
			TrustManager[] trustManagers = initTrustManagers(truststore, truststorePassword);

			SSLContext context = SSLContext.getInstance("TLS");
			context.init(keyManagers, trustManagers, null);

			return context;

		} catch (Exception e) {
			throw U.rte(e);
		}
	}

	private static KeyManager[] initKeyManagers(String keystore, char[] keystorePassword, char[] keyManagerPassword) throws Exception {
		KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(new FileInputStream(keystore), keystorePassword);

		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
		keyManagerFactory.init(keyStore, keyManagerPassword);

		return keyManagerFactory.getKeyManagers();
	}

	private static TrustManager[] initTrustManagers(String trustStoreFilename, char[] trustStorePassword) throws Exception {
		if (U.notEmpty(trustStoreFilename)) {

			U.notNull(trustStorePassword, "trustStorePassword");

			KeyStore trustStore = KeyStore.getInstance("JKS");
			trustStore.load(new FileInputStream(trustStoreFilename), trustStorePassword);

			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
			trustManagerFactory.init(trustStore);

			return trustManagerFactory.getTrustManagers();

		} else {
			return null;
		}
	}

}
