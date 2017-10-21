package com.example;

import org.rapidoid.setup.On;
import org.rapidoid.u.U;

public class HelloWorldExample {

	public static void main(String[] args) {
		// This starts a HTTP server on port 8080 and defines a handler for the route GET /hello
		On.get("/hello").json(() -> U.map("msg", "Hello, world!"));
	}

}

