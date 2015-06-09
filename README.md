Rapidoid - Simple, Powerful, Secure.
========

![Rapidoid showcase](http://www.rapidoid.org/showcase.gif)

# Apache License v2 

The main distribution of the framework is released under the liberal Apache Public License v2, so it is free to use for both commercial and non-commercial projects.

# Additional experimental modules (rapidoid-x-*) under AGPL

There are additional experimental modules (named rapidoid-x-*) under Affero GPL.

# Documentation (examples)

Please visit the official site:

[http://www.rapidoid.org/](http://www.rapidoid.org/#quickstart)

# Using with Maven

Add the following snippet to the `<dependencies>` section in pom.xml:

```xml
<dependency>
    <groupId>org.rapidoid</groupId>
    <artifactId>rapidoid-quick</artifactId>
    <version>3.0.0</version>
</dependency>
```

# Quick start

* Add the `rapidoid-quick` dependency to your Maven project (as described above).

* Add the file App.java:
 
```java

import org.rapidoid.annotation.*;
import org.rapidoid.plugins.DB;
import org.rapidoid.quick.Quick;

@RESTful
public class App {

	public static void main(String[] args) {
		Quick.run(args);
	}

	@GET("/books")
	public Object index() {
		return DB.getAll(Book.class);
	}

	@GET("/newbook") // for debugging
	@POST("/newbook")
	public Object addBook(String title, int year) {
		Book b = new Book();
		b.title = title;
		b.year = year;
		return DB.insert(b);
	}

}
```

* Add the file Book.java:
 
```java

import javax.persistence.Entity;
import org.rapidoid.jpa.JPAEntity;

@Entity
@SuppressWarnings("serial")
public class Book extends JPAEntity {

	String title;

	int year;

}
```

Run the App class. Rapidoid will scan your classpath, detect JPA entities, REST services, GUI elements and it will start a fast embedded HTTP server.

* Navigate to:
 * [http://localhost:8080/](http://localhost:8080/)

# More about REST services

* Add the MainService.java file:
 
```java
import java.util.List;
import java.util.Map;

@RESTful("/")
public class MainService {

    public String index() {
        return "hi!";
    }

    public String hey(String name, int age) {
        return "Hey " + name + " (" + age + ")";
    }

    public List<String> foo(List<String> params) {
        return params;
    }

    public Map<String, Object> bar(Map<String, Object> params) {
        return params;
    }

}
```

Rapidoid will scan for `*Service` classes on your classpath and start a fast embedded HTTP server (`rapidoid-http`).

* Navigate to:
 * [http://localhost:8080/](http://localhost:8080/) (returns `hi!`)
 * [http://localhost:8080/hey/joe/22](http://localhost:8080/hey/joe/22) (returns `Hey joe (22)`)
 * [http://localhost:8080/foo/aa/bbb/c](http://localhost:8080/foo/aa/bbb/c) (returns `["aa","bbb","c"]`)
 * [http://localhost:8080/bar?x=1&y=2](http://localhost:8080/bar?x=1&y=2) (returns `{"y":"2","x":"1"}`)

# Contributing

1. Fork (and then `git clone https://github.com/<your-username-here>/rapidoid.git`).
2. Create a branch (`git checkout -b branch_name`).
3. Commit your changes (`git commit -am "Description of contribution"`).
4. Push the branch (`git push origin branch_name`).
5. Open a Pull Request.
6. Thank you for your contribution! Wait for a response...

