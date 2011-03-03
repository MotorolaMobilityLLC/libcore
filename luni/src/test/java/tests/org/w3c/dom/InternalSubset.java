
/*
This Java source file was generated by test-to-java.xsl
and is a derived work from the source document.
The source document contained the following notice:



Copyright (c) 2001-2004 World Wide Web Consortium,
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

import org.w3c.dom.DocumentType;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;

/**
 *     The "getInternalSubset()" method returns
 *    the internal subset as a string or null if there is none.
 *    This does not contain the delimiting brackets.
 *
 *    Retrieve the documenttype.
 *    Apply the "getInternalSubset()" method.  Null is returned since there
 *    is not an internal subset.
* @author NIST
* @author Mary Brady
* @see <a href="http://www.w3.org/TR/DOM-Level-2-Core/core#ID-Core-DocType-internalSubset">http://www.w3.org/TR/DOM-Level-2-Core/core#ID-Core-DocType-internalSubset</a>
*/
public final class InternalSubset extends DOMTestCase {

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
   public void testGetInternalSubset() throws Throwable {
      Document doc;
      DocumentType docType;
      String internal;
      doc = (Document) load("staff2", builder);
      docType = doc.getDoctype();
      internal = docType.getInternalSubset();
      assertNull("internalSubsetNull", internal);
      }

}

