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

import org.apache.commons.vfs2.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public final class FileObjectUtils {

    private static final Logger log = LoggerFactory
            .getLogger(FileObjectUtils.class);

    private static FileSystemManager fsManager;

    private static FileSystemOptions fileSystemOptions;

    private FileObjectUtils() {
    }

    public static final void setFileSystemOptions(FileSystemOptions options) {
        fileSystemOptions = options;
    }

    public static final FileObject resolveFile(String location, String baseDir)
            throws FileSystemException {

        if (location.toLowerCase().startsWith("res:")
                || location.toLowerCase().startsWith("tmp:")
                || location.toLowerCase().startsWith("zip:"))
            return resolveFile(location);

        try {

            new URL(location); // try to determine, if location is a
            // valid url? if not, exception will
            // be thrown

            return resolveFile(location);

        } catch (MalformedURLException e1) {

            return resolveFile(baseDir + "/" + location);

        }

    }

    public static final FileObject resolveFile(String location)
            throws FileSystemException {

        log.debug("Resolving file object: {}", location);

        if (fsManager == null)
            fsManager = VFS.getManager();

        if (fileSystemOptions != null)
            return fsManager.resolveFile(location, fileSystemOptions);

        return fsManager.resolveFile(location);
    }

    public static final void closeFileObjectQuietly(FileObject fo) {
        if (fo != null) {
            try {
                fo.close();
            } catch (FileSystemException e) {
                log.error("Error closing file object: " + fo, e);
            }
        }

    }

}
