/*
 * Copyright 2013 Maxim Kalina
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
import org.apache.commons.vfs2.FileObject;
import org.apache.xerces.impl.dv.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XSLTUtils {

    private static final Logger log = LoggerFactory.getLogger(XSLTUtils.class);

    public static final String toBase64(String baseDir, String location) {

        try {

            FileObject fo = FileObjectUtils.resolveFile(location, baseDir);

            byte[] data = IOUtils.toByteArray(fo.getContent().getInputStream());

            StringBuffer sb = new StringBuffer();
            sb.append("data:");
            sb.append(determineMimeType(location));
            sb.append(";base64,");
            sb.append(Base64.encode(data));

            fo.close();
            return sb.toString();

        } catch (Exception e) {
            log.error("Error reading image file: " + location, e);
        }

        return location;
    }

    public static final String dumpCss(String baseDir, String location) {

        try {

            FileObject fo = FileObjectUtils.resolveFile(location, baseDir);

            StringBuffer sb = new StringBuffer();
            sb.append("<!--\n");
            sb.append(IOUtils.toString(fo.getContent().getInputStream()))
                    .append("\n");
            sb.append("-->\n");

            fo.close();

            return sb.toString();

        } catch (Exception e) {
            log.error("Error reading css file: " + location, e);
        }

        return "";

    }

    private static final String determineMimeType(String location) {

        String s = location.toLowerCase().trim();
        if (s.endsWith("png"))
            return "image/png";

        if (s.endsWith("gif"))
            return "image/gif";

        if (s.endsWith("jpg") || s.endsWith("jpeg"))
            return "image/jpeg";

        return "image/gif"; // default

    }
}
