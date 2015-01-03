package abc;

/*
 * #%L
 * rapidoid-compile
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
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.rapidoid.util.U;

public class Main {

	public static void main(String[] args) {
		Runnable r1 = new Runnable() {
			@Override
			public void run() {
				U.info("hello1!");
			}
		};

		r1.run();
		U.info("success!");
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
			U.show(key, value);
		}
		return new Date();
	}

}
