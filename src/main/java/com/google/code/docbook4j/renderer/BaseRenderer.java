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
import com.google.code.docbook4j.ExpressionEvaluatingXMLReader;
import com.google.code.docbook4j.FileObjectUtils;
import com.google.code.docbook4j.XslURIResolver;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

abstract class BaseRenderer<T extends BaseRenderer<T>> implements Renderer<T> {

    private static final Logger log = LoggerFactory
            .getLogger(BaseRenderer.class);

    protected String xmlResource;

    protected String xslResource;

    protected Map<String, String> params = new HashMap<String, String>();

    protected Map<String, Object> vars = new HashMap<String, Object>();

    @SuppressWarnings("unchecked")
    public T xml(String xmlResource) {
        this.xmlResource = xmlResource;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T xsl(String xslResource) {
        this.xslResource = xslResource;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T fileSystemOptions(FileSystemOptions fileSystemOptions) {
        FileObjectUtils.setFileSystemOptions(fileSystemOptions);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T parameter(String name, String value) {
        this.params.put(name, value);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T parameters(Map<String, String> parameters) {
        if (parameters != null)
            this.params.putAll(parameters);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T variable(String name, Object value) {
        this.vars.put(name, value);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T variables(Map<String, Object> values) {
        if (values != null)
            this.vars.putAll(values);
        return (T) this;
    }

    public InputStream render() throws Docbook4JException {

        assertNotNull(xmlResource,
                "Value of the xml source should be not null!");

        FileObject xsltResult = null;
        FileObject xmlSourceFileObject = null;
        FileObject xslSourceFileObject = null;

        try {

            xmlSourceFileObject = FileObjectUtils.resolveFile(xmlResource);
            if (xslResource != null) {
                xslSourceFileObject = FileObjectUtils.resolveFile(xslResource);
            } else {
                xslSourceFileObject = getDefaultXslStylesheet();
            }

            SAXParserFactory factory = createParserFactory();
            final XMLReader reader = factory.newSAXParser().getXMLReader();

            EntityResolver resolver = new EntityResolver() {
                public InputSource resolveEntity(String publicId,
                                                 String systemId) throws SAXException, IOException {

                    log.debug("Resolving file {}", systemId);

                    FileObject inc = FileObjectUtils.resolveFile(systemId);
                    return new InputSource(inc.getContent().getInputStream());
                }
            };

            // prepare xml sax source
            ExpressionEvaluatingXMLReader piReader = new ExpressionEvaluatingXMLReader(
                    reader, vars);
            piReader.setEntityResolver(resolver);

            SAXSource source = new SAXSource(piReader, new InputSource(
                    xmlSourceFileObject.getContent().getInputStream()));
            source.setSystemId(xmlSourceFileObject.getURL().toExternalForm());

            // prepare xslt result
            xsltResult = FileObjectUtils.resolveFile("tmp://"
                    + UUID.randomUUID().toString());
            xsltResult.createFile();

            // create transofrmer and do transformation
            final Transformer transformer = createTransformer(
                    xmlSourceFileObject, xslSourceFileObject);
            transformer.transform(source, new StreamResult(xsltResult
                    .getContent().getOutputStream()));

            // do post processing
            FileObject target = postProcess(xmlSourceFileObject,
                    xslSourceFileObject, xsltResult);

            FileObjectUtils.closeFileObjectQuietly(xsltResult);
            FileObjectUtils.closeFileObjectQuietly(target);
            return target.getContent().getInputStream();

        } catch (FileSystemException e) {
            throw new Docbook4JException("Error transofrming xml!", e);
        } catch (SAXException e) {
            throw new Docbook4JException("Error transofrming xml!", e);
        } catch (ParserConfigurationException e) {
            throw new Docbook4JException("Error transofrming xml!", e);
        } catch (TransformerException e) {
            throw new Docbook4JException("Error transofrming xml!", e);
        } catch (IOException e) {
            throw new Docbook4JException("Error transofrming xml !", e);
        } finally {
            FileObjectUtils.closeFileObjectQuietly(xmlSourceFileObject);
            FileObjectUtils.closeFileObjectQuietly(xslSourceFileObject);
        }

    }

    protected FileObject postProcess(FileObject xmlSource,
                                     FileObject xslSource, FileObject xsltResult)
            throws Docbook4JException {
        return xsltResult;
    }

    protected SAXParserFactory createParserFactory() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setXIncludeAware(true);
        factory.setNamespaceAware(true);
        return factory;
    }

    protected TransformerFactory createTransformerFactory() {
        return TransformerFactory.newInstance();
    }

    protected Transformer createTransformer(FileObject xmlSource,
                                            FileObject xslStylesheet) throws TransformerConfigurationException,
            IOException {

        TransformerFactory transformerFactory = createTransformerFactory();
        if (xslStylesheet != null) {
            transformerFactory.setURIResolver(new XslURIResolver());
        }
        FileObject xsl = xslStylesheet != null ? xslStylesheet
                : getDefaultXslStylesheet();

        Source source = new StreamSource(xsl.getContent().getInputStream(), xsl
                .getURL().toExternalForm());
        Transformer transformer = transformerFactory.newTransformer(source);

        transformer.setParameter("use.extensions", "1");
        transformer.setParameter("callout.graphics", "0");
        transformer.setParameter("callout.unicode", "1");
        transformer.setParameter("callouts.extension", "1");
        transformer.setParameter("base.dir", xmlSource.getParent().getURL()
                .toExternalForm());

        for (Map.Entry<String, String> entry : this.params.entrySet()) {
            transformer.setParameter(entry.getKey(), entry.getValue());
        }

        return transformer;
    }

    protected abstract FileObject getDefaultXslStylesheet();

    protected FileObject resolveXslStylesheet(String location) {
        try {
            return FileObjectUtils.resolveFile(location);

        } catch (FileSystemException e) {
            throw new IllegalStateException("Error resolving xsl stylesheet: "
                    + location, e);
        }

    }

    private void assertNotNull(Object value, String message) {
        if (value == null)
            throw new IllegalArgumentException(message);
    }

}
