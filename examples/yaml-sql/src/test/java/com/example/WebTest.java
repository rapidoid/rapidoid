package com.example;

import org.junit.Test;
import org.rapidoid.annotation.IntegrationTest;
import org.rapidoid.data.JSON;
import org.rapidoid.http.Self;
import org.rapidoid.io.IO;
import org.rapidoid.jdbc.JDBC;
import org.rapidoid.test.RapidoidIntegrationTest;

/**
 * This test will execute the main class specified in the annotation.
 */
@IntegrationTest(main = YamlSqlExample.class)
public class WebTest extends RapidoidIntegrationTest {

	@Test
	public void testHelloWorld() {
		// initialize the database
		JDBC.execute("init.sql");

		insertBook(10, "Java");
		insertBook(20, "Scala");
		insertBook(30, "Kotlin");
		insertBook(40, "Groovy");
		insertBook(50, "Python");

		// send some HTTP requests and checks the results

		Object expected = JSON.parse(IO.load("expected-books.json"));
		eq(expected, Self.get("/books").parse());
	}

	private void insertBook(int id, String title) {
		JDBC.execute("INSERT INTO books VALUES (?, ?)", id, title);
	}

}
