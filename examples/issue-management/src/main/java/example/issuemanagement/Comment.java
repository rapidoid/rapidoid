package example.issuemanagement;

import static org.rapidoid.util.CommonRoles.*;

import org.rapidoid.annotation.Programmatic;
import org.rapidoid.app.entity.SimpleUser;
import org.rapidoid.security.annotation.Change;
import org.rapidoid.security.annotation.Read;

@Read({ LOGGED_IN })
public class Comment {

	@Change({ OWNER, MODERATOR })
	public String content;

	@Change({ MODERATOR })
	@Read({ MODERATOR })
	public boolean visible = true;

	@Programmatic
	public SimpleUser owner;

}
