/*-
 * #%L
 * rapidoid-test-commons
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

package org.rapidoid.test;

import java.lang.reflect.Method;

/**
 * @author Nikolche Mihajlovski
 * @since 6.0.0
 */
public class TestContext {

    private final Class<?> testClass;

    private final Method testMethod;

    public TestContext(Class<?> testClass, Method testMethod) {
        this.testClass = testClass;
        this.testMethod = testMethod;
    }

    public Class<?> testClass() {
        return testClass;
    }

    public Method testMethod() {
        return testMethod;
    }

}
