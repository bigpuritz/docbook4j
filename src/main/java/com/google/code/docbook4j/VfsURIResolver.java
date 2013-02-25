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

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

public class VfsURIResolver implements URIResolver {

    private static final Logger log = LoggerFactory
            .getLogger(VfsURIResolver.class);

    public Source resolve(String href, String base) throws TransformerException {
        log.debug("Resolving href={} for base={}", href, base);

        try {

            FileObject urlFileObject = FileObjectUtils.resolveFile(href, base);
            return new StreamSource(urlFileObject.getContent().getInputStream());

        } catch (FileSystemException e) {

            log.warn("Error resolving href=" + href + " for base=" + base, e);
            return null;
        }
    }

}
