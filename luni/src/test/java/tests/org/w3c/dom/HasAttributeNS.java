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

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;

/**
 *
 * The "hasAttributeNS()" method for an Element should return false if the
 * element does not have an attribute with the given local name and/or a
 * namespace URI specified on this element or does not have a default value.
 * Retrieve the first "address" element and the "hasAttributeNS()" method should
 * return false since the element has "nomatch" as the local name and
 * "http://www.usa.com" as the namespace URI.
 *
 * @author NIST
 * @author Mary Brady
 * @see <a
 *      href="http://www.w3.org/TR/DOM-Level-2-Core/core#ID-ElHasAttrNS">http://www.w3.org/TR/DOM-Level-2-Core/core#ID-ElHasAttrNS</a>
 */
public final class HasAttributeNS extends DOMTestCase {

    DOMDocumentBuilderFactory factory;

    DocumentBuilder builder;

    protected void setUp() throws Exception {
        super.setUp();
        try {
            factory = new DOMDocumentBuilderFactory(DOMDocumentBuilderFactory
                    .getConfiguration2());
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
    public void testHasAttributeNS1() throws Throwable {
        String localName = "nomatch";
        String namespaceURI = "http://www.usa.com";
        Document doc;
        NodeList elementList;
        Element testNode;
        boolean state;
        doc = (Document) load("staffNS", builder);
        elementList = doc.getElementsByTagName("address");
        testNode = (Element) elementList.item(0);
        state = testNode.hasAttributeNS(namespaceURI, localName);
        assertFalse("throw_False", state);
    }
    public void testHasAttributeNS2() throws Throwable {
        String localName = "domestic";
        String namespaceURI = "http://www.nomatch.com";
        Document doc;
        NodeList elementList;
        Element testNode;
        boolean state;
        doc = (Document) load("staffNS", builder);
        elementList = doc.getElementsByTagName("address");
        testNode = (Element) elementList.item(0);
        state = testNode.hasAttributeNS(namespaceURI, localName);
        assertFalse("throw_False", state);
    }
    public void testHasAttributeNS3() throws Throwable {
        String localName = "blank";
        String namespaceURI = "http://www.nist.gov";
        Document doc;
        NodeList elementList;
        Element testNode;
        boolean state;
        doc = (Document) load("staffNS", builder);
        elementList = doc.getElementsByTagName("emp:address");
        testNode = (Element) elementList.item(0);
        assertNotNull("empAddrNotNull", testNode);
        state = testNode.hasAttributeNS(namespaceURI, localName);
        assertFalse("throw_False", state);
    }

// Assumes validation.
//    public void testHasAttributeNS4() throws Throwable {
//        String localName = "district";
//        String namespaceURI = "http://www.nist.gov";
//        Document doc;
//        NodeList elementList;
//        Element testNode;
//        boolean state;
//        doc = (Document) load("staffNS", builder);
//        elementList = doc.getElementsByTagName("emp:address");
//        testNode = (Element) elementList.item(0);
//        assertNotNull("empAddressNotNull", testNode);
//        state = testNode.hasAttributeNS(namespaceURI, localName);
//        assertTrue("hasAttribute", state);
//    }
    public void testHasAttributeNS5() throws Throwable {
        String localName = "domestic";
        String namespaceURI = "http://www.usa.com";
        Document doc;
        NodeList elementList;
        Element testNode;
        boolean state;
        doc = (Document) load("staffNS", builder);
        elementList = doc.getElementsByTagName("address");
        testNode = (Element) elementList.item(0);
        state = testNode.hasAttributeNS(namespaceURI, localName);
        assertTrue("hasAttribute", state);
    }
}
