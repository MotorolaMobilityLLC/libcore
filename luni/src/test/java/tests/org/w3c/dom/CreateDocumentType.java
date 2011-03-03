/*
 This Java source file was generated by test-to-java.xsl
 and is a derived work from the source document.
 The source document contained the following notice:



 Copyright (c) 2001 World Wide Web Consortium,
 (Massachusetts Institute of Technology, Institut National de
 Recherche en Informatique et en Automatique, Keio University).  All
 Rights Reserved.  This program is distributed under the W3C's Software
 Intellectual Property License.  This program is distributed in the
 hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 PURPOSE.

 See W3C License http://www.w3.org/Consortium/Legal/ for more details.


 */

package tests.org.w3c.dom;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentType;

import javax.xml.parsers.DocumentBuilder;

/**
 * The "createDocumentType(qualifiedName,publicId,systemId)" method for a
 * DOMImplementation should raise NAMESPACE_ERR DOMException if qualifiedName is
 * malformed.
 *
 * Retrieve the DOMImplementation on the XMLNS Document. Invoke method
 * createDocumentType(qualifiedName,publicId,systemId) on the retrieved
 * DOMImplementation with qualifiedName being the literal string
 * "prefix::local", publicId as "STAFF", and systemId as "staff". Method should
 * raise NAMESPACE_ERR DOMException.
 *
 * @author NIST
 * @author Mary Brady
 * @see <a
 *      href="http://www.w3.org/TR/DOM-Level-2-Core/core#xpointer(id('ID-258A00AF')/constant[@name='NAMESPACE_ERR'])">http://www.w3.org/TR/DOM-Level-2-Core/core#xpointer(id('ID-258A00AF')/constant[@name='NAMESPACE_ERR'])</a>
 * @see <a
 *      href="http://www.w3.org/TR/DOM-Level-2-Core/core#Level-2-Core-DOM-createDocType">http://www.w3.org/TR/DOM-Level-2-Core/core#Level-2-Core-DOM-createDocType</a>
 * @see <a
 *      href="http://www.w3.org/TR/DOM-Level-2-Core/core#xpointer(id('Level-2-Core-DOM-createDocType')/raises/exception[@name='DOMException']/descr/p[substring-before(.,':')='NAMESPACE_ERR'])">http://www.w3.org/TR/DOM-Level-2-Core/core#xpointer(id('Level-2-Core-DOM-createDocType')/raises/exception[@name='DOMException']/descr/p[substring-before(.,':')='NAMESPACE_ERR'])</a>
 */
public final class CreateDocumentType extends DOMTestCase {

    DOMDocumentBuilderFactory factory;

    DocumentBuilder builder;

    protected void setUp() throws Exception {
        super.setUp();
        try {
            factory = new DOMDocumentBuilderFactory(DOMDocumentBuilderFactory
                    .getConfiguration1());
            builder = factory.getBuilder();
        } catch (Exception e) {
            fail("Unexpected exception" + e.getMessage());
        }
    }

    protected void tearDown() throws Exception {
        factory = null;
        builder = null;
        super.tearDown();
    }

    /**
     * Runs the test case.
     *
     * @throws Throwable
     *             Any uncaught exception causes test to fail
     */
    public void testCreateDocumentType1() throws Throwable {
        String publicId = "STAFF";
        String systemId = "staff.xml";
        String malformedName = "prefix::local";
        Document doc;
        DOMImplementation domImpl;

        doc = (Document) load("staffNS", builder);
        domImpl = doc.getImplementation();

        {
            boolean success = false;
            try {
                domImpl.createDocumentType(malformedName, publicId, systemId);
            } catch (DOMException ex) {
                success = (ex.code == DOMException.NAMESPACE_ERR);
            }
            assertTrue("throw_NAMESPACE_ERR", success);
        }
    }
    public void testCreateDocumentType2() throws Throwable {
        String publicId = "http://www.localhost.com/";
        String systemId = "myDoc.dtd";
        String qualifiedName;
        Document doc;

        DOMImplementation domImpl;
        List<String> illegalQNames = new ArrayList<String>();
        illegalQNames.add("edi:{");
        illegalQNames.add("edi:}");
        illegalQNames.add("edi:~");
        illegalQNames.add("edi:'");
        illegalQNames.add("edi:!");
        illegalQNames.add("edi:@");
        illegalQNames.add("edi:#");
        illegalQNames.add("edi:$");
        illegalQNames.add("edi:%");
        illegalQNames.add("edi:^");
        illegalQNames.add("edi:&");
        illegalQNames.add("edi:*");
        illegalQNames.add("edi:(");
        illegalQNames.add("edi:)");
        illegalQNames.add("edi:+");
        illegalQNames.add("edi:=");
        illegalQNames.add("edi:[");
        illegalQNames.add("edi:]");
        illegalQNames.add("edi:\\");
        illegalQNames.add("edi:/");
        illegalQNames.add("edi:;");
        illegalQNames.add("edi:`");
        illegalQNames.add("edi:<");
        illegalQNames.add("edi:>");
        illegalQNames.add("edi:,");
        illegalQNames.add("edi:a ");
        illegalQNames.add("edi:\"");

        doc = (Document) load("staffNS", builder);
        for (int indexN1009A = 0; indexN1009A < illegalQNames.size(); indexN1009A++) {
            qualifiedName = (String) illegalQNames.get(indexN1009A);
            domImpl = doc.getImplementation();

            {
                boolean success = false;
                try {
                    domImpl.createDocumentType(qualifiedName, publicId,
                            systemId);
                } catch (DOMException ex) {
                    success = (ex.code == DOMException.INVALID_CHARACTER_ERR);
                }
                assertTrue("throw_INVALID_CHARACTER_ERR", success);
            }
        }
    }
    public void testCreateDocumentType3() throws Throwable {

        String qualifiedName = "prefix:myDoc";
        String publicId = "http://www.localhost.com";
        String systemId = "myDoc.dtd";
        Document doc;
        DOMImplementation domImpl;
        DocumentType newType = null;

        String nodeName;
        String nodeValue;
        doc = (Document) load("staffNS", builder);
        domImpl = doc.getImplementation();
        newType = domImpl.createDocumentType(qualifiedName, publicId, systemId);
        nodeName = newType.getNodeName();
        assertEquals("nodeName", "prefix:myDoc", nodeName);
        nodeValue = newType.getNodeValue();
        assertNull("nodeValue", nodeValue);
    }
    public void testCreateDocumentType4() throws Throwable {
        String publicId = "http://www.example.com/";
        String systemId = "myDoc.dtd";

        DOMImplementation domImpl;
        domImpl = builder.getDOMImplementation();

        {
            boolean success = false;
            try {
                domImpl.createDocumentType("", publicId, systemId);
            } catch (DOMException ex) {
                success = (ex.code == DOMException.NAMESPACE_ERR);
            }
            assertTrue("throw_NAMESPACE_ERR", success);
        }
    }
}
