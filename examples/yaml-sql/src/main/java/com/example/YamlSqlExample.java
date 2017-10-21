package com.example;

import org.rapidoid.jdbc.JDBC;
import org.rapidoid.setup.App;

public class YamlSqlExample {

	public static void main(String[] args) {
		App.init(args);

		// initialize the database (for demo only)
		JDBC.execute("sql/init.sql");
		insertSomeBooks();

		App.ready();
	}

	private static void insertSomeBooks() {
		insertBook(10, "Java");
		insertBook(20, "Scala");
		insertBook(30, "Kotlin");
		insertBook(40, "Groovy");
		insertBook(50, "Python");

//		insertMoreBooks(1000);
	}

	private static void insertMoreBooks(int count) {
		for (int i = 0; i < count; i++) {
			int bookId = 100 + i;
			insertBook(bookId, "Book " + bookId);
		}
	}

	private static void insertBook(int id, String title) {
		JDBC.execute("INSERT INTO books VALUES (?, ?)", id, title);
	}

}

