package com.example;

import org.junit.Test;
import org.rapidoid.http.Self;
import org.rapidoid.u.U;

import java.util.Map;

/**
 * The test will execute the main class specified in {@link AbstractIntegrationTest}.
 */
public class HelloWorldTest extends AbstractIntegrationTest {

	@Test
	public void testHelloWorld() {
		// connects to http://localhost:8080 and sends HTTP request GET /hello, then parses the JSON result as Map
		Map<String, Object> resp = Self.get("/hello").toMap();

		eq(resp, U.map("msg", "Hello, world!"));
	}

}
