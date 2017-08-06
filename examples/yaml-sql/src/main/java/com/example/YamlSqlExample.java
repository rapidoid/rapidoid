package com.example;

import org.rapidoid.jdbc.JDBC;
import org.rapidoid.setup.App;

public class YamlSqlExample {

	public static void main(String[] args) {
		App.init(args);
		JDBC.execute("sql/init.sql");
		App.ready();
	}

}

