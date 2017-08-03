package com.example;

import org.rapidoid.annotation.Service;
import org.rapidoid.jpa.JPA;
import org.rapidoid.log.Log;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class MyService {

	private volatile List<Book> initialBooks;

	@PostConstruct
	public void initializeData() {
		Log.info("Reading books from DB");
		initialBooks = JPA.of(Book.class).all();
	}

	public List<Book> getInitialBooks() {
		return initialBooks;
	}
}
