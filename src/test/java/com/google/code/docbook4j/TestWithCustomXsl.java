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

import com.google.code.docbook4j.renderer.HTMLRenderer;
import com.google.code.docbook4j.renderer.PDFRenderer;
import com.google.code.docbook4j.renderer.RTFRenderer;
import org.junit.Test;

import java.io.File;

public class TestWithCustomXsl extends BaseDocbook4JTest {

    private static final String source = "zip:"
            + new File("src/test/resources/testing-custom-xsl.zip")
            .getAbsolutePath() + "!testing/manual.xml";

    private static final String pdfXsl = "zip:"
            + new File("src/test/resources/testing-custom-xsl.zip")
            .getAbsolutePath() + "!testing/xsl/pdf/main-pdf.xsl";

    private static final String htmlXsl = "zip:"
            + new File("src/test/resources/testing-custom-xsl.zip")
            .getAbsolutePath() + "!testing/xsl/html/main-single.xsl";

    private static final String css = "zip:"
            + new File("src/test/resources/testing-custom-xsl.zip")
            .getAbsolutePath() + "!testing/css/html.css";

    @Override
    protected String getTargetFolder() {
        return "custom";
    }

    @Test
    public void testPDFGeneration() throws Throwable {
        PDFRenderer pdfRenderer = PDFRenderer.create(source, pdfXsl)
                .variable("project", Project.create());
        writeToFile(pdfRenderer.render(), "01.pdf");
    }

    @Test
    public void testRTFGeneration() throws Throwable {
        RTFRenderer rtfRenderer = RTFRenderer.create(source, pdfXsl).variable(
                "project", Project.create());
        writeToFile(rtfRenderer.render(), "01.rtf");
    }

    @Test
    public void testHTML() throws Throwable {
        HTMLRenderer htmlRendererCss = HTMLRenderer.create(source, htmlXsl)
                .variable("project", Project.create());
        writeToFile(htmlRendererCss.render(), "01.html");
    }

    @Test
    public void testHTMLWithCssGeneration() throws Throwable {
        HTMLRenderer htmlRendererCss = HTMLRenderer.create(source, htmlXsl)
                .css(css).variable("project", Project.create());
        writeToFile(htmlRendererCss.render(), "02.html");
    }


}
