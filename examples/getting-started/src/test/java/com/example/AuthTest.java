package com.example;

import org.junit.Test;
import org.rapidoid.http.Self;
import org.rapidoid.security.AuthResponse;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

public class AuthTest extends AbstractIntegrationTest {

	@Test
	public void testSuccessfulLogin() {
		AuthResponse login = Self.post(Msc.specialUri("login"))
			.data(U.map("username", "foo", "password", "foo"))
			.toBean(AuthResponse.class);

		isTrue(login.success);
		isTrue(U.notEmpty(login.token));
	}

	@Test
	public void testIncorrentLogin() {
		AuthResponse login = Self.post(Msc.specialUri("login"))
			.data(U.map("username", "foo", "password", "wrong"))
			.toBean(AuthResponse.class);

		isFalse(login.success);
		isTrue(U.isEmpty(login.token));
	}

}
