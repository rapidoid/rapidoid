package com.example;

import org.junit.Test;
import org.rapidoid.annotation.IntegrationTest;
import org.rapidoid.http.Self;
import org.rapidoid.test.RapidoidIntegrationTest;
import org.rapidoid.u.U;

/**
 * This test will execute the main class specified in the annotation.
 */
@IntegrationTest(main = Main.class)
public class WebTest extends RapidoidIntegrationTest {

	@Test
	public void testHelloWorld() {
		// connects to the local server that was started by the Main class (http://localhost:8080)
		// then sends HTTP requests and checks the results

		eq(U.list(U.map("Pi", 3.14)), Self.get("/pi").parse());
		eq(U.list(U.map("G", 9.81)), Self.get("/g").parse());
	}

}
