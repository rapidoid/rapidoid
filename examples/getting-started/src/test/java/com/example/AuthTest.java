package com.example;

import org.junit.Test;
import org.rapidoid.security.AuthResponse;
import org.rapidoid.u.U;

public class AuthTest extends AbstractIntegrationTest {

	@Test
	public void testSuccessfulLogin() {
		AuthResponse login = post("/_login")
			.data(U.map("username", "foo", "password", "foo"))
			.toBean(AuthResponse.class);

		isTrue(login.success);
		isTrue(U.notEmpty(login.token));
	}

	@Test
	public void testIncorrentLogin() {
		AuthResponse login = post("/_login")
			.data(U.map("username", "foo", "password", "wrong"))
			.toBean(AuthResponse.class);

		isFalse(login.success);
		isTrue(U.isEmpty(login.token));
	}

}
