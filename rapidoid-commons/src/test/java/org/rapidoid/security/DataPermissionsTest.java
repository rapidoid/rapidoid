package org.rapidoid.security;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Composite;
import org.rapidoid.annotation.Programmatic;
import org.rapidoid.annotation.Since;
import org.rapidoid.security.annotation.CanChange;
import org.rapidoid.security.annotation.CanRead;
import org.rapidoid.u.U;

import java.util.List;

import static org.rapidoid.security.Role.*;

@CanRead({ANYBODY})
@CanChange({ANYBODY})
class AbstrEntity {

	@CanChange({})
	public long id;

	@CanRead("abc")
	@CanChange({"OTHER_ROLE"})
	public String notes;

}

class Category {
	public String name;

	@CanChange("OTHER_ROLE")
	public String desc;
}

@CanRead({ANYBODY, "OTHER_ROLE"})
class Comment {

	@CanChange({MODERATOR, OWNER})
	public String content;

	@CanRead({MANAGER})
	@CanChange({MANAGER})
	public boolean visible = true;

	@Programmatic
	public String createdBy;

}

@CanRead({OWNER, SHARED_WITH, "OTHER_ROLE"})
@CanChange({OWNER})
class Issue extends AbstrEntity {

	public String title;

	public int year;

	public User author;

	public String description;

	@Composite
	@CanChange({OWNER, SHARED_WITH})
	public List<Comment> comments;

	@Programmatic
	public String createdBy;

	@CanChange({OWNER})
	public List<User> sharedWith;

}

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class DataPermissionsTest extends SecurityTestCommons {

	private static final String[] USERS = {null, "", "abc", "adm1", "adm2", "mng1", "mod1", "mod2"};

	@Test
	public void testCommentPermissions() {
		checkPermissions(null, Comment.class, "content", true, false);
		checkPermissions(null, Comment.class, "visible", false, false);
		checkPermissions(null, Comment.class, "createdBy", true, false);

		checkPermissions("", Comment.class, "content", true, false);
		checkPermissions("", Comment.class, "visible", false, false);
		checkPermissions("", Comment.class, "createdBy", true, false);

		checkPermissions("abc", Comment.class, "content", true, false);
		checkPermissions("abc", Comment.class, "visible", false, false);
		checkPermissions("abc", Comment.class, "createdBy", true, false);
	}

	@Test
	public void testIssuePermissions() {
		String[] fields = {"title", "year", "author", "description", "comments", "createdBy", "sharedWith"};

		for (String field : fields) {
			for (String user : USERS) {
				checkPermissions(user, Issue.class, field, false, false);
			}
			checkPermissions("foo", Issue.class, field, false, false);
			checkPermissions("bar", Issue.class, field, false, false);
			checkPermissions("other", Issue.class, field, true, false);
		}

		Issue issue = new Issue();

		for (String field : fields) {
			for (String user : USERS) {
				checkPermissions(user, Issue.class, issue, field, false, false);
			}
			checkPermissions("foo", Issue.class, issue, field, false, false);
			checkPermissions("bar", Issue.class, issue, field, false, false);
			checkPermissions("other", Issue.class, issue, field, true, false);
		}

		issue.createdBy = "the-owner";
		issue.sharedWith = U.list(new User("bar"));

		for (String field : fields) {
			for (String user : USERS) {
				checkPermissions(user, Issue.class, issue, field, false, false);
			}
			checkPermissions("the-owner", Issue.class, issue, field, true, true);
			if (field.equals("comments")) {
				checkPermissions("bar", Issue.class, issue, field, true, true);
			} else {
				checkPermissions("bar", Issue.class, issue, field, true, false);
			}
			checkPermissions("other", Issue.class, issue, field, true, false);
		}

		for (String user : USERS) {
			checkPermissions(user, Issue.class, issue, "id", true, false);
			checkPermissions(user, Issue.class, issue, "notes", U.eq(user, "abc"), U.eq(user, "other"));
		}
	}

	@Test
	public void testCategoryPermissions() {
		for (String user : USERS) {
			checkPermissions(user, Category.class, "name", true, true);
			checkPermissions(user, Category.class, "desc", true, false);
		}

		checkPermissions("other", Category.class, "desc", true, true);
	}

}
