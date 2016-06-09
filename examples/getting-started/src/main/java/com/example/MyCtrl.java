package com.example;

import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;
import org.rapidoid.security.annotation.Roles;

@Controller
public class MyCtrl {

	@GET("/")
	public Object home() {
		return "This is public!";
	}

	@GET
	@Roles("manager")
	public Object manage() {
		return "Welcome, Mr. Manager!";
	}

}
