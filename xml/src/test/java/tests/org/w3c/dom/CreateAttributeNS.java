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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Attr;

import javax.xml.parsers.DocumentBuilder;

@TestTargetClass(Document.class) 
public final class CreateAttributeNS extends DOMTestCase {

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
        notes = "Verifies NAMESPACE_ERR exception code.",
        method = "createAttributeNS",
        args = {java.lang.String.class, java.lang.String.class}
    )
    public void testCreateAttributeNS1() throws Throwable {
        String namespaceURI = "http://www.ecommerce.org/";
        String malformedName = "prefix::local";
        Document doc;

        doc = (Document) load("staffNS", builder);

        {
            boolean success = false;
            try {
                doc.createAttributeNS(namespaceURI, malformedName);
            } catch (DOMException ex) {
                success = (ex.code == DOMException.NAMESPACE_ERR);
            }
            assertTrue("throw_NAMESPACE_ERR", success);
        }
    }
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies createAttributeNS method with null as the fisrt parameter.",
        method = "createAttributeNS",
        args = {java.lang.String.class, java.lang.String.class}
    )
    public void testCreateAttributeNS2() throws Throwable {
        String namespaceURI = null;

        String qualifiedName = "prefix:local";
        Document doc;

        doc = (Document) load("staffNS", builder);

        {
            boolean success = false;
            try {
                doc.createAttributeNS(namespaceURI, qualifiedName);
            } catch (DOMException ex) {
                success = (ex.code == DOMException.NAMESPACE_ERR);
            }
            assertTrue("throw_NAMESPACE_ERR", success);
        }
    }
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies that createAttributeNS throws DOMException.",
        method = "createAttributeNS",
        args = {java.lang.String.class, java.lang.String.class}
    )
    public void testCreateAttributeNS3() throws Throwable {
        String namespaceURI = "http://www.wedding.com/";
        String qualifiedName;
        Document doc;

        List<String> illegalQNames = new ArrayList<String>();
        illegalQNames.add("person:{");
        illegalQNames.add("person:}");
        illegalQNames.add("person:~");
        illegalQNames.add("person:'");
        illegalQNames.add("person:!");
        illegalQNames.add("person:@");
        illegalQNames.add("person:#");
        illegalQNames.add("person:$");
        illegalQNames.add("person:%");
        illegalQNames.add("person:^");
        illegalQNames.add("person:&");
        illegalQNames.add("person:*");
        illegalQNames.add("person:(");
        illegalQNames.add("person:)");
        illegalQNames.add("person:+");
        illegalQNames.add("person:=");
        illegalQNames.add("person:[");
        illegalQNames.add("person:]");
        illegalQNames.add("person:\\");
        illegalQNames.add("person:/");
        illegalQNames.add("person:;");
        illegalQNames.add("person:`");
        illegalQNames.add("person:<");
        illegalQNames.add("person:>");
        illegalQNames.add("person:,");
        illegalQNames.add("person:a ");
        illegalQNames.add("person:\"");

        doc = (Document) load("staffNS", builder);
        for (int indexN10090 = 0; indexN10090 < illegalQNames.size(); indexN10090++) {
            qualifiedName = (String) illegalQNames.get(indexN10090);
            {
                boolean success = false;
                try {
                    doc.createAttributeNS(namespaceURI, qualifiedName);
                } catch (DOMException ex) {
                    success = (ex.code == DOMException.INVALID_CHARACTER_ERR);
                }
                assertTrue("throw_INVALID_CHARACTER_ERR", success);
            }
        }
    }
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Doesn't verify exceptions.",
        method = "createAttributeNS",
        args = {java.lang.String.class, java.lang.String.class}
    )
    public void testCreateAttributeNS4() throws Throwable {
        String namespaceURI = "http://www.w3.org/XML/1998/namespaces";
        String qualifiedName = "xml:attr1";
        Document doc;

        doc = (Document) load("staffNS", builder);

        {
            boolean success = false;
            try {
                doc.createAttributeNS(namespaceURI, qualifiedName);
            } catch (DOMException ex) {
                success = (ex.code == DOMException.NAMESPACE_ERR);
            }
            assertTrue("throw_NAMESPACE_ERR", success);
        }
    }
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Doesn't verify exceptions.",
        method = "createAttributeNS",
        args = {java.lang.String.class, java.lang.String.class}
    )
    public void testCreateAttributeNS5() throws Throwable {
        String namespaceURI = "http://www.ecommerce.org/";
        String qualifiedName = "econm:local";
        Document doc;
        Attr newAttr;
        String attrName;
        doc = (Document) load("staffNS", builder);
        newAttr = doc.createAttributeNS(namespaceURI, qualifiedName);
        attrName = newAttr.getName();
        assertEquals("throw_Equals", qualifiedName, attrName);
    }
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "",
        method = "createAttributeNS",
        args = {java.lang.String.class, java.lang.String.class}
    )
    public void testCreateAttributeNS6() throws Throwable {
        String namespaceURI = "http://www.example.com/";
        Document doc;

        doc = (Document) load("hc_staff", builder);

        boolean success = false;
        try {
            doc.createAttributeNS(namespaceURI, "");
        } catch (DOMException ex) {
            success = (ex.code == DOMException.NAMESPACE_ERR);
        }
        assertTrue("throw_INVALID_CHARACTER_ERR", success);
    }
}
