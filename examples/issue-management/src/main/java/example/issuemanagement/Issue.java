package example.issuemanagement;

import static org.rapidoid.util.CommonRoles.*;

import java.util.List;

import org.rapidoid.annotation.Programmatic;
import org.rapidoid.app.Scaffold;
import org.rapidoid.app.entity.Entity;
import org.rapidoid.app.entity.SimpleUser;
import org.rapidoid.security.User;
import org.rapidoid.security.annotation.Change;
import org.rapidoid.security.annotation.LoggedIn;
import org.rapidoid.security.annotation.Read;

@Scaffold
@LoggedIn
@Read({ OWNER, SHARED_WITH })
@Change({ OWNER, SHARED_WITH })
public class Issue extends Entity {

	@Change(OWNER)
	public String title;

	public int year;

	@Programmatic
	public SimpleUser author;

	public String description;

	@Change(LOGGED_IN)
	public List<Comment> comments;

	@Programmatic
	public User owner;

	@Programmatic
	public List<User> sharedWith;

}
