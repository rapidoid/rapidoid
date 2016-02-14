package com.example;

import org.rapidoid.http.On;
import org.rapidoid.http.Req;

public class Main {

	public static void main(String[] args) {
		On.get("/").json((Req req) -> req.params());
		On.req(req -> req.uri());
	}

}
