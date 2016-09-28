package com.example;

import org.rapidoid.annotation.Run;
import org.rapidoid.annotation.Valid;
import org.rapidoid.jpa.JPA;
import org.rapidoid.log.Log;
import org.rapidoid.setup.App;
import org.rapidoid.setup.My;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

@Run
public class Main {

	public static void main(String[] args) {
		Log.info("Starting application");

		App.bootstrap(args).jpa().auth(); // bootstrap controllers, JPA and Auth

		On.get("/books").json(() -> JPA.of(Book.class).all()); // get all books

		On.post("/books").json((@Valid Book b) -> JPA.save(b)); // insert new book

		On.put("/books/{id}").json((Long id, @Valid Book b) -> JPA.update(b)); // update (replace) book

		On.delete("/books/{id}").json((Long id) -> { // delete book
			JPA.delete(Book.class, id);
			return true;
		});

		// Dummy login: successful if the username is the same as the password
		My.loginProvider((req, username, password) -> username.equals(password));

		// Gives the 'manager' role to every logged-in user
		My.rolesProvider((req, username) -> U.set("manager"));
	}

}

