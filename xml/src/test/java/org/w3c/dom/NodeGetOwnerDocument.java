/*
 This Java source file was generated by test-to-java.xsl
 and is a derived work from the source document.
 The source document contained the following notice:



 Copyright (c) 2001-2003 World Wide Web Consortium, 
 (Massachusetts Institute of Technology, Institut National de
 Recherche en Informatique et en Automatique, Keio University).  All 
 Rights Reserved.  This program is distributed under the W3C's Software
 Intellectual Property License.  This program is distributed in the 
 hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 PURPOSE.  

 See W3C License http://www.w3.org/Consortium/Legal/ for more details.


 */

package org.w3c.dom;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

/**
 * The method getOwnerDocument returns the Document object associated with this
 * node
 * 
 * Create a new DocumentType node. Since this node is not used with any Document
 * yet verify if the ownerDocument is null.
 * 
 * @author IBM
 * @author Neil Delima
 * @see <a
 *      href="http://www.w3.org/TR/DOM-Level-2-Core/core#node-ownerDoc">http://www.w3.org/TR/DOM-Level-2-Core/core#node-ownerDoc</a>
 * @see <a
 *      href="http://www.w3.org/Bugs/Public/show_bug.cgi?id=259">http://www.w3.org/Bugs/Public/show_bug.cgi?id=259</a>
 */
public final class NodeGetOwnerDocument extends DOMTestCase {

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
    public void testGetOwnerDocument1() throws Throwable {
        Document doc;
        Document ownerDoc;
        DOMImplementation domImpl;
        DocumentType docType;
        String nullID = null;

        doc = (Document) load("staff", builder);
        domImpl = doc.getImplementation();
        docType = domImpl.createDocumentType("mydoc", nullID, nullID);
        ownerDoc = docType.getOwnerDocument();
        assertNull("nodegetownerdocument01", ownerDoc);
    }

    public void testGetOwnerDocument2() throws Throwable {
        Document doc;
        Document newDoc;
        Element newElem;
        Document ownerDocDoc;
        Document ownerDocElem;
        DOMImplementation domImpl;
        DocumentType docType;
        String nullNS = null;

        doc = (Document) load("staff", builder);
        domImpl = doc.getImplementation();
        docType = domImpl.createDocumentType("mydoc", nullNS, nullNS);
        newDoc = domImpl.createDocument("http://www.w3.org/DOM/Test", "mydoc",
                docType);
        ownerDocDoc = newDoc.getOwnerDocument();
        assertNull("nodegetownerdocument02_1", ownerDocDoc);
        newElem = newDoc
                .createElementNS("http://www.w3.org/DOM/Test", "myelem");
        ownerDocElem = newElem.getOwnerDocument();
        assertNotNull("nodegetownerdocument02_2", ownerDocElem);
    }
}
