package org.rapidoid.net.tls;

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
import org.rapidoid.u.U;
import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Date;

@Authors("Nikolche Mihajlovski")
@Since("5.4.0")
public class SelfSignedCertGen extends RapidoidThing {

	@SuppressWarnings("restriction")
	public static void generate(SelfSignedCertInfo info, String keystore, char[] keystorePassword) throws Exception {

		U.must(U.notEmpty(info.alias()), "The alias must be specified!");
		U.must(info.password() != null, "The password must be specified!");

		CertAndKeyGen certAndKeyGen = new CertAndKeyGen("RSA", "SHA1WithRSA", null);
		certAndKeyGen.generate(info.keysize());

		long validityInSeconds = info.validity() * 24 * 3600;
		X509Certificate[] cert = {certAndKeyGen.getSelfCertificate(x500Name(info), new Date(), validityInSeconds)};

		KeyStore store = KeyStore.getInstance("JKS");

		if (new File(keystore).exists()) {
			store.load(new FileInputStream(keystore), keystorePassword);
		} else {
			store.load(null, null);
		}

		store.setKeyEntry(info.alias(), certAndKeyGen.getPrivateKey(), info.password(), cert);
		store.store(new FileOutputStream(keystore), keystorePassword);
	}

	private static X500Name x500Name(SelfSignedCertInfo info) throws IOException {
		return new X500Name(info.name(), info.unit(), info.organization(), info.locality(), info.state(), info.country());
	}

}
