package com.example;

import org.rapidoid.http.fast.On;

public class Main {

	public static void main(String[] args) {
		On.get("/").json(req -> req.params());
		On.req(req -> req.uri());
	}

}
