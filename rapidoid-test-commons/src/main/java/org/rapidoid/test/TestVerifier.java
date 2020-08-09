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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Nikolche Mihajlovski
 * @since 6.0.0
 */
public class TestVerifier {

    private final TestContext context;

    private final String projectionsPath;

    private final boolean adjustTests;

    private final TestComparator comparator;

    public TestVerifier(TestContext context, String projectionsPath, boolean adjustTests, TestComparator comparator) {
        this.context = context;
        this.projectionsPath = projectionsPath;
        this.adjustTests = adjustTests;
        this.comparator = comparator;
    }

    public void verify(String actual) {
        verify("result", actual);
    }

    public void verifyCase(String info, String actual, String testCaseName) {
        String sep = File.separator;

        String resName = projectionsPath
                + sep + context.testClass().getSimpleName()
                + sep + context.testMethod().getName()
                + sep + testCaseName;

        String filename = "src" + sep + "test" + sep + "resources" + sep + resName;

        if (adjustTests) {
            synchronized (this) {
                File testDir = new File(filename).getParentFile();

                if (!testDir.exists()) {
                    if (!testDir.mkdirs()) {
                        throw new RuntimeException("Couldn't create the test result folder: " + testDir.getAbsolutePath());
                    }
                }

                FileOutputStream out;
                try {
                    out = new FileOutputStream(filename);
                    out.write(actual.getBytes());
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        } else {
            byte[] bytes = TestIO.loadRes(resName);
            String expected = bytes != null ? new String(bytes) : "";
            comparator.check(info, actual, expected);
        }
    }

    public void verify(String name, String actual) {
        verifyCase(name, actual, name);
    }

}
