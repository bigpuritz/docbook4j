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

package com.google.code.docbook4j.renderer;

import com.google.code.docbook4j.Docbook4JException;
import com.google.code.docbook4j.FileObjectUtils;
import com.google.code.docbook4j.VfsURIResolver;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.util.UUID;

abstract class FORenderer<T extends FORenderer<T>> extends BaseRenderer<T> {

    private static final String defaultXslStylesheet = "res:xsl/docbook/fo/docbook.xsl";

    @Override
    protected FileObject getDefaultXslStylesheet() {
        return resolveXslStylesheet(defaultXslStylesheet);
    }

    @Override
    protected FileObject postProcess(final FileObject xmlSource,
                                     final FileObject xslSource, final FileObject xsltResult)
            throws Docbook4JException {

        FileObject target = null;
        try {

            final FopFactory fopFactory = FopFactory.newInstance();

            final FOUserAgent userAgent = fopFactory.newFOUserAgent();
            userAgent.setBaseURL(xmlSource.getParent().getURL()
                    .toExternalForm());
            userAgent.setURIResolver(new VfsURIResolver());

            enhanceFOUserAgent(userAgent);

            String tmpPdf = "tmp://" + UUID.randomUUID().toString();
            target = FileObjectUtils.resolveFile(tmpPdf);
            target.createFile();

            Configuration configuration = createFOPConfig();
            if (configuration != null) {
                fopFactory.setUserConfig(configuration);
            }

            Fop fop = fopFactory.newFop(getMimeType(), userAgent, target
                    .getContent().getOutputStream());

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(); // identity
            // transformer
            transformer.setParameter("use.extensions", "1");
            transformer.setParameter("fop.extensions", "0");
            transformer.setParameter("fop1.extensions", "1");

            Source src = new StreamSource(xsltResult.getContent()
                    .getInputStream());
            Result res = new SAXResult(fop.getDefaultHandler());
            transformer.transform(src, res);
            return target;

        } catch (FileSystemException e) {
            throw new Docbook4JException("Error create filesystem manager!", e);
        } catch (TransformerException e) {
            throw new Docbook4JException("Error transforming fo to pdf!", e);
        } catch (FOPException e) {
            throw new Docbook4JException("Error transforming fo to pdf!", e);
        } finally {

            FileObjectUtils.closeFileObjectQuietly(target);
        }

    }

    protected void enhanceFOUserAgent(FOUserAgent userAgent) {
    }

    protected Configuration createFOPConfig() {
        return null;
    }

    protected abstract String getMimeType();

}
