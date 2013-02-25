Intention of this project is to create a simple embeddable java library able to render docbook documents
into well-known target formats (html, pdf, man,...).

Docbook4j 1.0.0 released! See https://code.google.com/p/docbook4j/downloads/list

Consult this blog post (http://blog.javaforge.net/post/37107285148/render-docbook-with-docbook4j)
for a more detailed usage explanation/tutorial.

Maven users please add following repository and dependency declarations to your POM-File:

```
<repositories>
 <repository>
  <id>googlecode</id>
  <url>http://docbook4j.googlecode.com/svn/m2-repo/releases/</url>
 </repository>
</repositories>

<dependency>
    <groupId>com.google.code.docbook4j</groupId>
    <artifactId>docbook4j</artifactId>
    <version>1.0.0</version>
</dependency>
```

Note! Docbook4j supports all filesystem types supported by commons-vfs2 (see http://commons.apache.org/vfs/filesystems.html)

Some usage examples:

```java
String xml = "zip:path/to/my/zip/docs.zip!document.xml";
PDFRenderer pdfRenderer = PDFRenderer.create(xml);
InputStream in = pdfRenderer.render();
```

```java
String xml = "res:my/classpath/document.xml";
String xsl = "zip:path/to/my/zip/xsls.zip!doc.xsl";
String css = "file:myth/to/my.css";
HTMLRenderer htmlRenderer = HTMLRenderer.create(xml, xsl).css(css).
InputStream in = htmlRenderer.render();
```