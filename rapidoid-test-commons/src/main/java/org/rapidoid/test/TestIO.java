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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Nikolche Mihajlovski
 * @since 6.0.0
 */
public class TestIO {

    public static byte[] loadRes(String filename) {
        InputStream input = TestIO.class.getClassLoader().getResourceAsStream(filename);

        if (input == null) {
            throw new RuntimeException("Cannot find resource: " + filename);
        }

        return readBytes(input);
    }

    public static byte[] readBytes(InputStream input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        byte[] buffer = new byte[16 * 1024];

        try {
            int readN;
            while ((readN = input.read(buffer)) != -1) {
                output.write(buffer, 0, readN);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return output.toByteArray();
    }

    public static void delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    delete(f);
                }
            }
        }

        if (!file.delete()) {
            throw new RuntimeException("Couldn't delete: " + file);
        }
    }

    public static File createTempFile() {
        File file;
        try {
            file = File.createTempFile("temp", "" + System.nanoTime());
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create temporary file!", e);
        }

        file.deleteOnExit();
        return file;
    }

    public static String createTempDir(String name) {
        Path tmpDir;
        try {
            tmpDir = Files.createTempDirectory(name);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create temporary directory!", e);
        }

        String tmpPath = tmpDir.toAbsolutePath().toString();
        tmpDir.toFile().deleteOnExit();
        return tmpPath;
    }

}
