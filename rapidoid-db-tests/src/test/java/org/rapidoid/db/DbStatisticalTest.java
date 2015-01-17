package org.rapidoid.db;

/*
 * #%L
 * rapidoid-db-tests
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

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.rapidoid.db.model.Person;
import org.rapidoid.lambda.Operation;
import org.rapidoid.log.Log;
import org.rapidoid.log.LogLevel;
import org.rapidoid.util.Conf;
import org.rapidoid.util.U;
import org.testng.annotations.Test;

public class DbStatisticalTest extends DbTestCommons {

	class Op {
		final int op = rnd(3);
		final String name = rndStr(0, 100);
		final int age = rnd();
		final long id = rnd((int) (DB.size() * 2) + 10);
		final Person person = new Person(name, age);
		final boolean fail = rnd(30) == 0;
	}

	class Ret {
		long id = -1;
		boolean illegalId = false;
		boolean ok = false;
	}

	private final Map<Object, Object> persons = Collections.synchronizedMap(U.map());

	@Test
	public void testDbOperations() {

		Log.setLogLevel(LogLevel.SEVERE);
		
		U.benchmarkMT(Conf.cpus(), "op", 50000, new Runnable() {
			@Override
			public synchronized void run() {

				int n = rnd(10) + 1;
				final Op[] ops = new Op[n];
				final Ret[] rets = new Ret[n];

				for (int i = 0; i < ops.length; i++) {
					ops[i] = new Op();
					rets[i] = new Ret();
				}

				if (yesNo()) {

					final AtomicBoolean complete = new AtomicBoolean(false);

					DB.transaction(new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < ops.length; i++) {

								try {
									doDbOp(ops[i], rets[i]);
								} catch (IllegalArgumentException e) {
									rets[i].illegalId = true;
									throw U.rte(e);
								}

								if (ops[i].fail) {
									throw U.rte("err");
								}
							}

							complete.set(true);
						}
					}, false, null);

					for (int i = 0; i < ops.length; i++) {
						if (ops[i].fail || !rets[i].ok || rets[i].illegalId) {
							return;
						}
					}

					U.must(complete.get());

					for (int i = 0; i < ops.length; i++) {
						doShadowOp(ops[i], rets[i]);
					}

				} else {
					try {
						doDbOp(ops[0], rets[0]);
					} catch (IllegalArgumentException e) {
						rets[0].illegalId = true;
					}

					doShadowOp(ops[0], rets[0]);
				}
			}
		});

		DB.shutdown();

		System.out.println("Comparing data...");
		compareData();
		System.out.println("Total " + persons.size() + " records.");

	}

	private void compareData() {
		eq(DB.size(), persons.size());
		DB.each(new Operation<Person>() {
			@Override
			public void execute(Person p) throws Exception {
				Person p2 = (Person) persons.get(p.id);
				eq(p2.id, p.id);
				eq(p2.age, p.age);
				eq(p2.name, p.name);
			}
		});
	}

	private void doDbOp(Op op, Ret ret) {
		switch (op.op) {
		case 0:
			ret.id = DB.insert(op.person);
			break;

		case 1:
			DB.delete(op.id);
			break;

		case 2:
			DB.update(op.id, op.person);
			break;

		default:
			throw U.notExpected();
		}
		ret.ok = true;
	}

	private void doShadowOp(Op op, Ret ret) {
		switch (op.op) {
		case 0:
			assert persons.put(ret.id, op.person) == null;
			break;

		case 1:
			if (!ret.illegalId) {
				assert persons.remove(op.id) != null;
			} else {
				assert !persons.containsKey(op.id);
			}
			break;

		case 2:
			if (!ret.illegalId) {
				assert persons.put(op.id, op.person) != null;
			} else {
				assert !persons.containsKey(op.id);
			}
			break;

		default:
			throw U.notExpected();
		}
	}

}
