/*
 * Copyright 2013 [name of copyright owner]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.code.docbook4j;


import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public abstract class BaseDocbook4JTest {

    private static final Logger log = LoggerFactory
            .getLogger(TestWithCustomXsl.class);


    protected void writeToFile(InputStream in, String filename) throws Throwable {

        File targetDir = new File("target");
        targetDir.mkdirs();

        FileOutputStream out = new FileOutputStream(new File(targetDir, filename));
        long count = IOUtils.copyLarge(in, out);
        Assert.assertTrue(count > 0);
        log.info("bytes copied: {} ", count);
        out.close();
        in.close();
    }

    static final class Project {

        private String groupId;

        private String artifactId;

        private String version;

        private String name;

        private String inceptionYear;

        public static final Project create() {
            return new Project("com.google.code", "docbook4j", "1.1.1",
                    "Test Project", "2012");
        }

        public Project() {
        }

        public Project(String groupId, String artifactId, String version,
                       String name, String inceptionYear) {
            super();
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.version = version;
            this.name = name;
            this.inceptionYear = inceptionYear;
        }

        public String getInceptionYear() {
            return this.inceptionYear;
        }

        public void setInceptionYear(String inceptionYear) {
            this.inceptionYear = inceptionYear;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGroupId() {
            return this.groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getArtifactId() {
            return this.artifactId;
        }

        public void setArtifactId(String artifactId) {
            this.artifactId = artifactId;
        }

        public String getVersion() {
            return this.version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

    }

}
