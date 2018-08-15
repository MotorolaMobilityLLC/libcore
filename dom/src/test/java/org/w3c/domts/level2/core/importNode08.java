
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

package org.w3c.domts.level2.core;

import org.w3c.dom.*;


import org.w3c.domts.DOMTestCase;
import org.w3c.domts.DOMTestDocumentBuilderFactory;



/**
 *     The "importNode(importedNode,deep)" method for a 
 *    Document should import the given importedNode into that Document.
 *    The importedNode is of type Document_Fragment.
 *    
 *    Create a DocumentFragment in a different document.
 *    Invoke method importNode(importedNode,deep) on this document
 *    with importedNode being the newly created DocumentFragment.
 *    Method should return an empty DocumentFragment that belongs
 *    to this document whose systemId is "staff.dtd" 
* @author NIST
* @author Mary Brady
* @see <a href="http://www.w3.org/TR/DOM-Level-2-Core/core#Core-Document-importNode">http://www.w3.org/TR/DOM-Level-2-Core/core#Core-Document-importNode</a>
* @see <a href="http://www.w3.org/TR/DOM-Level-2-Core/core#ID-Core-DocType-systemId">http://www.w3.org/TR/DOM-Level-2-Core/core#ID-Core-DocType-systemId</a>
*/
public final class importNode08 extends DOMTestCase {

   /**
    * Constructor.
    * @param factory document factory, may not be null
    * @throws org.w3c.domts.DOMTestIncompatibleException Thrown if test is not compatible with parser configuration
    */
   public importNode08(final DOMTestDocumentBuilderFactory factory)  throws org.w3c.domts.DOMTestIncompatibleException {
      super(factory);

    //
    //   check if loaded documents are supported for content type
    //
    String contentType = getContentType();
    preload(contentType, "staffNS", true);
    preload(contentType, "staffNS", true);
    }

   /**
    * Runs the test case.
    * @throws Throwable Any uncaught exception causes test to fail
    */
   public void runTest() throws Throwable {
      Document doc;
      Document aNewDoc;
      DocumentFragment docFrag;
      Node aNode;
      boolean hasChild;
      Document ownerDocument;
      DocumentType docType;
      String system;
      doc = (Document) load("staffNS", true);
      aNewDoc = (Document) load("staffNS", true);
      docFrag = aNewDoc.createDocumentFragment();
      aNode = doc.importNode(docFrag, false);
      hasChild = aNode.hasChildNodes();
      assertFalse("hasChild", hasChild);
ownerDocument = aNode.getOwnerDocument();
      docType = ownerDocument.getDoctype();
      system = docType.getSystemId();
      assertURIEquals("system", null, null, null, "staffNS.dtd", null, null, null, null, system);
}
   /**
    *  Gets URI that identifies the test.
    *  @return uri identifier of test
    */
   public String getTargetURI() {
      return "http://www.w3.org/2001/DOM-Test-Suite/level2/core/importNode08";
   }
   /**
    * Runs this test from the command line.
    * @param args command line arguments
    */
   public static void main(final String[] args) {
        DOMTestCase.doMain(importNode08.class, args);
   }
}

