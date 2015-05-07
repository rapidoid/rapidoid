package org.rapidoidx.demo.taskplanner.gui;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.app.Screen;
import org.rapidoid.html.Tag;
import org.rapidoid.widget.CardWidget;
import org.rapidoid.widget.FA;
import org.rapidoid.widget.StreamWidget;

/*
 * #%L
 * rapidoid-x-demo
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class HomeScreen extends Screen {

	public String title = "Welcome!";

	public Object content() {
		boolean fav = true;
		Tag star = fav ? FA.STAR : FA.STAR_O;
		Tag upvote = FA.THUMBS_O_UP;
		Tag downvote = FA.THUMBS_O_DOWN;

		CardWidget card = card(h3("{{it().priority}} priority"), h4("Desc: {{it().description}}"));
		card = card.controls(upvote, "123", downvote, star);
		card = card.header("{{it().title}}");

		StreamWidget stream = stream(card).cols(3).dataUrl("/task/page/{{page}}");

		return arr(cmd("Refresh").info(), stream);
	}

}
