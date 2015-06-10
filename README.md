Rapidoid - Simple. Powerful. Secure. Fast!
========

![Rapidoid showcase](http://www.rapidoid.org/showcase.gif)


# Quick start: GUI + CRUD Scaffolding + RESTful services + JPA (Hibernate + in-mem H2SQL)   

* Add the following snippet to the `<dependencies>` section in pom.xml:

```xml
<dependency>
    <groupId>org.rapidoid</groupId>
    <artifactId>rapidoid-quick</artifactId>
    <version>3.0.0</version>
</dependency>
```

* Add the file App.java:
 
```java

import org.rapidoid.annotation.*;
import org.rapidoid.app.AppGUI;
import org.rapidoid.plugins.DB;
import org.rapidoid.quick.Quick;

@RESTful
public class App extends AppGUI {

	String title = "Library";
	Object content = panel("Welcome to the library!").header("Welcome!");

	public static void main(String[] args) {
		Quick.run(args);
	}

	@GET("/allbooks")
	public Object index() {
		return DB.getAll(Book.class); // using JPA (Hibernate)
	}

	@GET("/addbook") // for debugging
	@POST("/addbook")
	public Object addBook(Book book) {
		return DB.insert(book); // using JPA (Hibernate)
	}

}
```

* Add the file Book.java:
 
```java

import javax.persistence.Entity;
import org.rapidoid.annotation.Scaffold;
import org.rapidoid.jpa.JPAEntity;

@Entity
@Scaffold
@SuppressWarnings("serial")
public class Book extends JPAEntity {

	String title;

	int year;

}
```

Run the App class. No need to use any web container. Rapidoid will scan your classpath, detect *JPA* entities, *RESTful* services, *GUI* elements and it will start a *fast* embedded *HTTP* server + embedded *in-memory* *H2SQL* database for *quick* development and prototyping.

* Navigate to:
 * [http://localhost:8080/](http://localhost:8080/)
 * [http://localhost:8080/addbook?title=book1&year=2015](http://localhost:8080/addbook?title=book1&year=2015)
 * [http://localhost:8080/addbook?title=book2&year=2013](http://localhost:8080/addbook?title=book2&year=2013)
 * [http://localhost:8080/allbooks](http://localhost:8080/allbooks)

# More about RESTful services

* Add the MainService.java file:
 
```java
import org.rapidoid.annotation.*;
import java.util.List;
import java.util.Map;

@RESTful("/")
public class MainService {

    @GET
    public String hey(String name, int age) {
        return "Hey " + name + " (" + age + ")";
    }

    @GET
    public List<String> foo(List<String> params) {
        return params;
    }

    @GET("/barbar")
    public Map<String, Object> bar(Map<String, Object> params) {
        return params;
    }

}
```

Run the App class. No need to use any web container. Rapidoid will scan your classpath, detect *JPA* entities, *RESTful* services, *GUI* elements and it will start a *fast* embedded *HTTP* server + embedded *in-memory* *H2SQL* database for *quick* development and prototyping.

* Navigate to:
 * [http://localhost:8080/hey/joe/22](http://localhost:8080/hey/joe/22) (returns `Hey joe (22)`)
 * [http://localhost:8080/foo/aa/bbb/c](http://localhost:8080/foo/aa/bbb/c) (returns `["aa","bbb","c"]`)
 * [http://localhost:8080/barbar?x=1&y=2](http://localhost:8080/barbar?x=1&y=2) (returns `{"y":"2","x":"1"}`)

# Apache License v2

The main distribution of the framework is released under the liberal Apache Public License v2, so it is free to use for both commercial and non-commercial projects.

# Additional experimental modules (rapidoid-x-*) under AGPL

There are additional experimental modules (named rapidoid-x-*) under Affero GPL.

# Documentation (examples)

Please visit the official site:

[http://www.rapidoid.org/](http://www.rapidoid.org/#quickstart)

# Contributing

1. Fork (and then `git clone https://github.com/<your-username-here>/rapidoid.git`).
2. Create a branch (`git checkout -b branch_name`).
3. Commit your changes (`git commit -am "Description of contribution"`).
4. Push the branch (`git push origin branch_name`).
5. Open a Pull Request.
6. Thank you for your contribution! Wait for a response...

