package com.example;

import org.rapidoid.annotation.Valid;
import org.rapidoid.log.Log;
import org.rapidoid.security.Auth;
import org.rapidoid.setup.App;
import org.rapidoid.setup.Apps;
import org.rapidoid.setup.My;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

public class GettingStartedExample {

    public static void main(String[] args) {
        Apps.init(args, "secret=YOUR-SECRET");

        Log.info("Starting application");

        App app = new App();
        app.beans(new MyCtrl()); // provide beans (controllers, services etc.)

        On.get("/books").json(() -> {
            // TODO get all books
            return U.list();
        });

        On.post("/books").json((@Valid Book book) -> {
            // TODO insert new book
            return book;
        });

        On.put("/books/{id}").json((Long id, @Valid Book book) -> {
            // TODO update/replace book
            return true;
        });

        On.delete("/books/{id}").json((Long id) -> {
            // TODO delete book
            return true;
        });

        // Dummy login: successful if the username is the same as the password, or a proper password is entered
        My.loginProvider((req, username, password) -> username.equals(password) || Auth.login(username, password));

        // Gives the 'manager' role to every logged-in user except 'admin'
        My.rolesProvider((req, username) -> U.eq(username, "admin") ? Auth.getRolesFor(username) : U.set("manager"));

        Apps.ready(); // now everything is ready, so start the application
    }

}

