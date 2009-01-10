/*
 This Java source file was generated by test-to-java.xsl
 and is a derived work from the source document.
 The source document contained the following notice:


 Copyright (c) 2004 World Wide Web Consortium,
 (Massachusetts Institute of Technology, Institut National de
 Recherche en Informatique et en Automatique, Keio University). All
 Rights Reserved. This program is distributed under the W3C's Software
 Intellectual Property License. This program is distributed in the
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

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMException;

import javax.xml.parsers.DocumentBuilder;

/**
 * An attempt to add remove an notation using removeNamedItemNS should result in
 * a NO_MODIFICATION_ERR or a NOT_FOUND_ERR.
 * 
 * @author Curt Arnold
 * @see <a
 *      href="http://www.w3.org/TR/DOM-Level-2-Core/core#ID-D46829EF">http://www.w3.org/TR/DOM-Level-2-Core/core#ID-D46829EF</a>
 * @see <a
 *      href="http://www.w3.org/TR/DOM-Level-2-Core/core#ID-removeNamedItemNS">http://www.w3.org/TR/DOM-Level-2-Core/core#ID-removeNamedItemNS</a>
 */
@TestTargetClass(NamedNodeMap.class) 
public final class HCNotationsRemoveNamedItemNS extends DOMTestCase {

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
        notes = "Verifies that removeNamedItemNS method throws DOMException.",
        method = "removeNamedItemNS",
        args = {java.lang.String.class, java.lang.String.class}
    )
    public void testRemoveNamedItemNS() throws Throwable {
        Document doc;
        NamedNodeMap notations;
        DocumentType docType;

        doc = (Document) load("hc_staff", builder);
        docType = doc.getDoctype();

        if (!(("text/html".equals(getContentType())))) {
            assertNotNull("docTypeNotNull", docType);
            notations = docType.getNotations();
            assertNotNull("notationsNotNull", notations);

            try {
                notations.removeNamedItemNS("http://www.w3.org/1999/xhtml",
                        "alpha");
                fail("throw_NO_MOD_OR_NOT_FOUND_ERR");

            } catch (DOMException ex) {
                switch (ex.code) {
                case 7:
                    break;
                case 8:
                    break;
                default:
                    throw ex;
                }
            }
        }
    }

}
