
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

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

import javax.xml.parsers.DocumentBuilder;

/**
 *     The "getOwnerDocument()" method returns null if the target
 *     node itself is a DocumentType which is not used with any document yet.
 *
 *     Invoke the "getOwnerDocument()" method on the master
 *     document.   The DocumentType returned should be null.
* @author NIST
* @author Mary Brady
* @see <a href="http://www.w3.org/TR/DOM-Level-2-Core/core#node-ownerDoc">http://www.w3.org/TR/DOM-Level-2-Core/core#node-ownerDoc</a>
*/
public final class OwnerDocument extends DOMTestCase {

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
    * @throws Throwable Any uncaught exception causes test to fail
    */
   public void testGetOwnerDocument() throws Throwable {
      Document doc;
      DocumentType ownerDocument;
      doc = (Document) load("staff", builder);
      ownerDocument = (DocumentType) doc.getOwnerDocument();
      assertNull("throw_Null", ownerDocument);
      }

}

