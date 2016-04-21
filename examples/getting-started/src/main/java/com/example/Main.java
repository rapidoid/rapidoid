package com.example;

import org.rapidoid.http.Req;
import org.rapidoid.setup.On;

public class Main {

	public static void main(String[] args) {
		On.get("/").json((Req req) -> req.params());
		On.req(req -> req.uri());
	}

}
