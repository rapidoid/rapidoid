Rapidoid
========

Rapidoid aims to be the Coolest Full-stack Web Application Framework for Java!

# Super-simple. Powerful. Secure. Cool! #

![Rapidoid showcase](http://www.rapidoid.org/showcase.gif)

Teaser:

```java
@DbEntity
@Scaffold
@CanRead(LOGGED_IN)
@CanChange({ OWNER })
@CanInsert(LOGGED_IN)
@CanDelete({ OWNER, ADMIN })
public class Task {

	public long id; // primary key

	public long version; // optimistic CC

	@Display
	@CanChange({ MODERATOR, OWNER })
	public String title;

	@Display
	@CanChange({ MODERATOR, OWNER, SHARED_WITH })
	public Priority priority = Priority.MEDIUM;

	@Optional
	@CanChange({ MODERATOR, OWNER, SHARED_WITH })
	public String description;

	public int rating;

	@Programmatic
	public final DbRef<User> owner = DB.ref(this, "^owns");

	@CanRead({ OWNER })
	public final DbSet<User> sharedWith = DB.set(this, "sharedWith");

	@Programmatic
	@CanRead({ OWNER, SHARED_WITH })
	public final DbList<Comment> comments = DB.list(this, "has");

	@Programmatic
	public final DbSet<User> likedBy = DB.set(this, "^likes");

}
```

# Apache License v2

The software is released under the liberal Apache Public License v2, so it is free to use for both commercial and non-commercial projects.

# Step-by-step introduction

A step-by-step tutorial is available at the official site:
[http://www.rapidoid.org/](http://www.rapidoid.org/)

# Using with Maven

Add the following snippet to the `<dependencies>` section in pom.xml:

```xml
<dependency>
    <groupId>org.rapidoid</groupId>
    <artifactId>rapidoid-all</artifactId>
    <version>2.1.1</version>
</dependency>
```

# Quick start

* Add the `rapidoid-all` dependency to your Maven project (as described above).

* Add the file App.java:
 
```java
import org.rapidoid.app.Apps;

public class App {

	public String title = "Issue Management";

	public boolean full = true;

	public boolean fluid = false;

	public String theme = "1";

	public static void main(String[] args) {
		Apps.run(args);
	}

}
```

* Add the file HomeScreen.java:
 
```java
import org.rapidoid.app.Screen;
import org.rapidoid.db.DB;

public class HomeScreen extends Screen {

	public Object content() {
		return render("home.html", "count", DB.getAll(Issue.class).size());
	}

}
```

* Add the file Issue.java:
 
```java

import org.rapidoid.annotation.*;

@Scaffold
@DbEntity
public class Issue {

	public long id;

	public String title;

	public Priority priority = Priority.MEDIUM;

	@Optional
	public String description;

}
```

* Add the file Priority.java:
 
```java
public enum Priority {
	LOW, MEDIUM, HIGH;
}

```

* Add the file NewIssueScreen.java:
 
```java

import org.rapidoid.annotation.Session;
import org.rapidoid.app.Screen;
import org.rapidoid.db.DB;
import org.rapidoid.html.Tag;
import org.rapidoid.widget.FormWidget;

public class NewIssueScreen extends Screen {

	@Session
	public Issue issue = new Issue();

	public Object content() {
		Tag title = h2("Add new issue").style("margin-bottom:15px");
		FormWidget form = create(issue).buttons(SAVE, CANCEL);
		return mid6(title, form);
	}

	public void onSave() {
		DB.insert(issue);
		issue = new Issue();
	}

	public void onCancel() {
		issue = new Issue();
	}

}
```

Rapidoid will scan for `*Screen` classes on your classpath and will construct a nice and responsive GUI. It will start a fast embedded HTTP server (`rapidoid-http`).

* Navigate to:
 * [http://localhost:8080/](http://localhost:8080/)

# REST services

* Add the MainService.java file:
 
```java
import java.util.List;
import java.util.Map;

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

