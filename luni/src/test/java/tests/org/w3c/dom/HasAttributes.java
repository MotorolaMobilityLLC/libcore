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

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;

/**
 * The "hasAttributes()" method for a node should return false if the node does
 * not have an attribute. Retrieve the first "name" node and invoke the
 * "hasAttributes()" method. The method should return false since the node does
 * not have an attribute.
 *
 * @author NIST
 * @author Mary Brady
 * @see <a
 *      href="http://www.w3.org/TR/DOM-Level-2-Core/core#ID-NodeHasAttrs">http://www.w3.org/TR/DOM-Level-2-Core/core#ID-NodeHasAttrs</a>
 */
@TestTargetClass(Node.class)
public final class HasAttributes extends DOMTestCase {

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
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies that hasAttributes method returns false value.",
        method = "hasAttributes",
        args = {}
    )
    public void testHasAttributes1() throws Throwable {
        Document doc;
        NodeList addrList;
        Node addrNode;
        boolean state;
        doc = (Document) load("staff", builder);
        addrList = doc.getElementsByTagName("name");
        addrNode = addrList.item(0);
        state = addrNode.hasAttributes();
        assertFalse("throw_False", state);
    }
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies that hasAttributes method returns true value.",
        method = "hasAttributes",
        args = {}
    )
    public void testHasAttributes2() throws Throwable {
        Document doc;
        NodeList addrList;
        Node addrNode;
        boolean state;
        doc = (Document) load("staff", builder);
        addrList = doc.getElementsByTagName("address");
        addrNode = addrList.item(0);
        state = addrNode.hasAttributes();
        assertTrue("throw_True", state);
    }

}
