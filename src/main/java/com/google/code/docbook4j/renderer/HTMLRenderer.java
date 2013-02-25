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

import org.apache.commons.vfs2.FileObject;

public class HTMLRenderer extends BaseRenderer<HTMLRenderer> {

    private static final String defaultXslStylesheet = "res:xsl/docbook/xhtml/docbook.xsl";

    private HTMLRenderer() {
    }

    public HTMLRenderer css(String cssResource) {
        super.parameter("html.stylesheet", cssResource);
        return this;
    }

    @Override
    protected FileObject getDefaultXslStylesheet() {
        return resolveXslStylesheet(defaultXslStylesheet);
    }

    public static final HTMLRenderer create() {
        return new HTMLRenderer();
    }

    public static final HTMLRenderer create(String xmlResource) {
        return new HTMLRenderer().xml(xmlResource);
    }

    public static final HTMLRenderer create(String xmlResource,
                                            String xslResource) {
        return new HTMLRenderer().xml(xmlResource).xsl(xslResource);
    }

    public static final HTMLRenderer create(String xmlResource,
                                            String xslResource, String cssResource) {
        return new HTMLRenderer().xml(xmlResource).xsl(xslResource)
                .css(cssResource);
    }

}
