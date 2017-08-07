package com.example;

import org.junit.Test;
import org.rapidoid.annotation.IntegrationTest;
import org.rapidoid.data.JSON;
import org.rapidoid.http.Self;
import org.rapidoid.io.IO;
import org.rapidoid.test.RapidoidIntegrationTest;

/**
 * This test will execute the main class specified in the annotation.
 */
@IntegrationTest(main = YamlSqlExample.class)
public class WebTest extends RapidoidIntegrationTest {

	@Test
	public void testHelloWorld() {

		// send some HTTP requests and checks the results

		Object expected = JSON.parse(IO.load("expected-books.json"));
		eq(expected, Self.get("/books").parse());
	}

}
