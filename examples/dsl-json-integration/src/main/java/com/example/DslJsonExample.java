package com.example;

import org.rapidoid.setup.On;
import org.rapidoid.u.U;

public class DslJsonExample {

	public static void main(String[] args) {
		// a blank dsl-json integration example

		On.get("/hello").json(() -> U.map("msg", "Hello, world!"));

		// TODO integrate the dsl-json library
	}

}

