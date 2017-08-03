package com.example;

import org.junit.Test;
import org.rapidoid.annotation.IntegrationTest;
import org.rapidoid.http.Self;
import org.rapidoid.test.RapidoidIntegrationTest;
import org.rapidoid.u.U;

import java.util.Map;

/**
 * This test will execute the main class specified in the annotation.
 */
@IntegrationTest(main = HelloWorldExample.class)
public class WebTest extends RapidoidIntegrationTest {

	@Test
	public void testHelloWorld() {
		// connects to the local server that was started by the Main class (http://localhost:8080)
		// then sends HTTP request GET /hello and parses the JSON result as Map
		Map<String, Object> resp = Self.get("/hello").toMap();

		eq(resp, U.map("msg", "Hello, world!"));
	}

	@Test
	public void testHomeNotFound() {
		int code = Self.get("/").execute().code();

		eq(code, 404);
	}

}
