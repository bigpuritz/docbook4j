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

public class XslURIResolver implements URIResolver {

    private static final Logger log = LoggerFactory
            .getLogger(XslURIResolver.class);

    private String docbookXslBase;

    public Source resolve(String href, String base) throws TransformerException {

        log.debug("Resolving href={} for base={}", href, base);

        if (href == null || href.trim().length() == 0)
            return null;

        if (docbookXslBase == null && href.startsWith("res:")
                && href.endsWith("docbook.xsl")) {
            try {
                docbookXslBase = FileObjectUtils.resolveFile(href).getParent()
                        .getURL().toExternalForm();
            } catch (FileSystemException e) {
                docbookXslBase = null;
            }
        }

        String normalizedBase = null;
        if (base != null) {
            try {
                normalizedBase = FileObjectUtils.resolveFile(base).getParent()
                        .getURL().toExternalForm();
            } catch (FileSystemException e) {
                normalizedBase = null;
            }
        }

        try {

            FileObject urlFileObject = FileObjectUtils.resolveFile(href,
                    normalizedBase);

            if (!urlFileObject.exists())
                throw new FileSystemException("File object not found: "
                        + urlFileObject);

            return new StreamSource(
                    urlFileObject.getContent().getInputStream(), urlFileObject
                    .getURL().toExternalForm());

        } catch (FileSystemException e) {

            // not exists for given base? try with docbook base...
            try {
                if (docbookXslBase != null) {
                    FileObject urlFileObject = FileObjectUtils.resolveFile(
                            href, docbookXslBase);
                    return new StreamSource(urlFileObject.getContent()
                            .getInputStream(), urlFileObject.getURL()
                            .toExternalForm());
                }

            } catch (FileSystemException e1) {
                // do nothing.
            }

            log.error("Error resolving href=" + href + " for base=" + base, e);
        }

        return null;

    }
}
