package abc;

/*
 * #%L
 * rapidoid-x-compile
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.rapidoid.util.U;
import org.rapidoid.log.Log;

@mixo.Mixin
@Resource
public class Main {

	@Resource
	public String ggg;

	@Resource
	public void setMm(int mm) {
	}

	public static void main(String[] args) {
		Runnable r1 = new Runnable() {
			@Override
			public void run() {
				Log.info("hello1!");
			}
		};

		r1.run();
		Log.info("success!");
	}
}

class Person {

	class Insider {
	}

	String name = "";

	private int age = 0;

	public Person() {
	}

	public Person(String name, int age) {
		this.name = name;
		this.age = age;
	}

}

class PersonService {

	public Person insert(Person person) {
		return person;
	}

	public Object now(Map<String, Object> params) {
		for (Entry<?, ?> entry : params.entrySet()) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			D.print(key, value);
		}
		return new Date();
	}

}
