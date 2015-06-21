package demo.taskplanner.gui;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.app.Screen;
import org.rapidoid.html.Tag;
import org.rapidoid.widget.CardWidget;
import org.rapidoid.widget.FA;
import org.rapidoid.widget.StreamWidget;

/*
 * #%L
 * rapidoid-demo
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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
