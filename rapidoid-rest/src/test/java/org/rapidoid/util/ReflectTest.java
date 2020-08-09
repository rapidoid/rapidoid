/*-
 * #%L
 * rapidoid-rest
 * %%
 * Copyright (C) 2014 - 2020 Nikolche Mihajlovski and contributors
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

package org.rapidoid.util;

import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.*;
import org.rapidoid.cls.Cls;
import org.rapidoid.cls.TypeKind;
import org.rapidoid.http.HttpVerb;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;

import java.lang.reflect.Method;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

@Authors("Nikolche Mihajlovski")
@Since("6.0.0")
public class ReflectTest extends TestCommons {

    @Test
    public void testInstanceMethodParams() {
        Method m = U.single(Cls.getMethodsNamed(Foo.class, "abc"));
        String[] names = Reflect.getMethodParameterNames(m);
        eq(names, U.array("aa", "b", "чачача"));
    }

    @Test
    public void testStaticMethodParams() {
        Method m = U.single(Cls.getMethodsNamed(Foo.class, "statico"));
        String[] names = Reflect.getMethodParameterNames(m);
        eq(names, U.array("foo", "arg1"));
    }

    @Test
    public void testWithManyParams() {
        Method m = U.single(Cls.getMethodsNamed(Foo.class, "xy"));
        String[] names = Reflect.getMethodParameterNames(m);
        eq(names, U.array("a", "b", "c", "d", "e", "f"));

        Method m2 = U.single(Cls.getMethodsNamed(Foo.class, "xyz"));
        String[] names2 = Reflect.getMethodParameterNames(m2);
        eq(names2, U.array("a", "b", "c", "d", "e", "f", "g", "hh", "ii", "j"));
    }

}

class Foo {

    private synchronized void abc(String aa, int b, boolean чачача) {
        int x = 1, y = 12;
        String h = "foo";
    }

    private synchronized static void statico(String foo, boolean arg1) {
        int x = 1, y = 12;
        String h = "foo";
    }

    public static String xy(byte a, short b, char c, int d, long e, float f) {
        return null;
    }

    @Page
    @GET
    @DELETE
    @SuppressWarnings("unchecked")
    public String xyz(@Param("a") byte a, @Param("b") short b, @Param("c") char c, @Param("d") int d,
                      @Param("e") long e, @Param("f") float f, @Param("g") double g, @Param("h") boolean hh,
                      @Param("i") boolean ii, @Param("j") String j) {
        return U.join(":", a, b, c, d, e, f, g, hh, ii, j);
    }

}
