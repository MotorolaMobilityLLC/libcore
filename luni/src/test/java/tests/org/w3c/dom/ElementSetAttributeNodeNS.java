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

import dalvik.annotation.TestTargets;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargetClass;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.DOMException;
import org.w3c.dom.EntityReference;

import javax.xml.parsers.DocumentBuilder;

/**
 * Testing Element.setAttributeNodeNS: If an attribute with that local name and
 * that namespace URI is already present in the element, it is replaced by the
 * new one. Create a new element and two new attribute nodes (in the same
 * namespace and same localNames). Add the two new attribute nodes to the
 * element node using the setAttributeNodeNS method. Check that only one
 * attribute is added, check the value of this attribute.
 *
 * @author IBM
 * @author Neil Delima
 * @see <a
 *      href="http://www.w3.org/TR/DOM-Level-2-Core/core#ID-ElSetAtNodeNS">http://www.w3.org/TR/DOM-Level-2-Core/core#ID-ElSetAtNodeNS</a>
 */
@TestTargetClass(Element.class)
public final class ElementSetAttributeNodeNS extends DOMTestCase {

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
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Doesn't verify DOMException.",
        method = "setAttributeNodeNS",
        args = {org.w3c.dom.Attr.class}
    )
    public void testSetAttributeNodeNS1() throws Throwable {
        Document doc;
        Element element;
        Attr attribute1;
        Attr attribute2;
        Attr attrNode;
        String attrName;
        String attrNS;

        NamedNodeMap attributes;

        int length;
        doc = (Document) load("staff", builder);
        element = doc.createElementNS("http://www.w3.org/DOM/Test/Level2",
                "new:element");
        attribute1 = doc.createAttributeNS("http://www.w3.org/DOM/Test/att1",
                "p1:att");
        attribute2 = doc.createAttributeNS("http://www.w3.org/DOM/Test/att1",
                "p2:att");
        attribute2.setValue("value2");
        element.setAttributeNodeNS(attribute1);
        element.setAttributeNodeNS(attribute2);
        attrNode = element.getAttributeNodeNS(
                "http://www.w3.org/DOM/Test/att1", "att");
        attrName = attrNode.getNodeName();
        attrNS = attrNode.getNamespaceURI();
        assertEquals("elementsetattributenodens01_attrName", "p2:att", attrName);
        assertEquals("elementsetattributenodens01_attrNS",
                "http://www.w3.org/DOM/Test/att1", attrNS);
        attributes = element.getAttributes();
        length = (int) attributes.getLength();
        assertEquals("length", 1, length);
    }
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Doesn't verify DOMException.",
        method = "setAttributeNodeNS",
        args = {org.w3c.dom.Attr.class}
    )
    public void testSetAttributeNodeNS2() throws Throwable {
        Document doc;
        Element element;
        Element element2;
        Attr attribute;
        Attr attributeCloned;
        Attr newAttr;
        NodeList elementList;
        String attrName;
        String attrValue;
        String nullNS = null;

        doc = (Document) load("staffNS", builder);
        elementList = doc.getElementsByTagNameNS("http://www.nist.gov",
                "address");
        element = (Element) elementList.item(1);
        attribute = element.getAttributeNodeNS(nullNS, "street");
        attributeCloned = (Attr) attribute.cloneNode(true);
        element2 = (Element) elementList.item(2);
        newAttr = element2.setAttributeNodeNS(attributeCloned);
        attrName = newAttr.getNodeName();
        attrValue = newAttr.getNodeValue();
        assertEquals("elementsetattributenodens02_attrName", "street", attrName);
        assertEquals("elementsetattributenodens02_attrValue", "Yes", attrValue);
    }
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies DOMException with INUSE_ATTRIBUTE_ERR code.",
        method = "setAttributeNodeNS",
        args = {org.w3c.dom.Attr.class}
    )
    public void testSetAttributeNodeNS3() throws Throwable {
        Document doc;
        Element element1;
        Element element2;
        Attr attribute;

        NodeList elementList;
        String nullNS = null;

        doc = (Document) load("staffNS", builder);
        elementList = doc.getElementsByTagNameNS("http://www.nist.gov",
                "address");
        element1 = (Element) elementList.item(1);
        attribute = element1.getAttributeNodeNS(nullNS, "street");
        element2 = (Element) elementList.item(2);

        {
            boolean success = false;
            try {
                element2.setAttributeNodeNS(attribute);
            } catch (DOMException ex) {
                success = (ex.code == DOMException.INUSE_ATTRIBUTE_ERR);
            }
            assertTrue("elementsetattributenodens03", success);
        }
    }
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies DOMException with INUSE_ATTRIBUTE_ERR code.",
        method = "setAttributeNodeNS",
        args = {org.w3c.dom.Attr.class}
    )
    public void testSetAttributeNodeNS4() throws Throwable {
        Document doc;
        Element element1;
        Element element2;
        Attr attribute;

        doc = (Document) load("staffNS", builder);
        element1 = doc.createElementNS("http://www.w3.org/DOM/Test", "elem1");
        element2 = doc.createElementNS("http://www.w3.org/DOM/Test", "elem2");
        attribute = doc.createAttributeNS("http://www.w3.org/DOM/Test", "attr");
        element1.setAttributeNodeNS(attribute);

        {
            boolean success = false;
            try {
                element2.setAttributeNodeNS(attribute);
            } catch (DOMException ex) {
                success = (ex.code == DOMException.INUSE_ATTRIBUTE_ERR);
            }
            assertTrue("elementsetattributenodens04", success);
        }
    }
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies DOMException with WRONG_DOCUMENT_ERR code.",
        method = "setAttributeNodeNS",
        args = {org.w3c.dom.Attr.class}
    )
    public void testSetAttributeNodeNS5() throws Throwable {
        Document doc;
        Document docAlt;
        Element element;
        Attr attribute;

        doc = (Document) load("staffNS", builder);
        docAlt = (Document) load("staffNS", builder);
        element = doc.createElementNS("http://www.w3.org/DOM/Test", "elem1");
        attribute = docAlt.createAttributeNS("http://www.w3.org/DOM/Test",
                "attr");

        {
            boolean success = false;
            try {
                element.setAttributeNodeNS(attribute);
            } catch (DOMException ex) {
                success = (ex.code == DOMException.WRONG_DOCUMENT_ERR);
            }
            assertTrue("throw_WRONG_DOCUMENT_ERR", success);
        }
    }
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies DOMException with NO_MODIFICATION_ALLOWED_ERR code.",
        method = "setAttributeNodeNS",
        args = {org.w3c.dom.Attr.class}
    )
    public void _testSetAttributeNodeNS6() throws Throwable {
        Document doc;
        Element element;
        Attr attribute;
        Attr attribute2;
        EntityReference entRef;
        NodeList elementList;

        doc = (Document) load("staffNS", builder);
        element = doc.createElementNS("http://www.w3.org/DOM/Test", "elem1");
        attribute = doc.createAttributeNS("http://www.w3.org/DOM/Test", "attr");
        entRef = doc.createEntityReference("ent4");
        attribute.appendChild(entRef);
        element.setAttributeNodeNS(attribute);
        elementList = entRef.getChildNodes();
        element = (Element) elementList.item(0);
        attribute2 = doc.createAttributeNS("http://www.w3.org/DOM/Test",
                "attr2");

        {
            boolean success = false;
            try {
                element.setAttributeNodeNS(attribute2);
            } catch (DOMException ex) {
                success = (ex.code == DOMException.NO_MODIFICATION_ALLOWED_ERR);
            }
            assertTrue("elementsetattributenodens06", success);
        }
    }

}
