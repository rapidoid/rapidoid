package com.example;

import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;
import org.rapidoid.security.annotation.Roles;
import org.rapidoid.u.U;

import javax.inject.Inject;
import java.util.List;

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

    @GET("/books/initial")
    public List<Book> initialBooks() {
        return U.list();
    }

}
