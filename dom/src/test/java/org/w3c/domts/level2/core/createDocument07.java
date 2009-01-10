
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
 *     The "createDocument(namespaceURI,qualifiedName,doctype)" method for a 
 *    DOMImplementation should return a new xml Document object of the 
 *    specified type with its document element given that all parameters are
 *    valid and correctly formed.
 *    
 *    Invoke method createDocument(namespaceURI,qualifiedName,doctype) on
 *    this domimplementation. namespaceURI is "http://www.ecommerce.org/schema"
 *    qualifiedName is "y:x" and doctype is null.
 *    Method should return a new xml Document as specified by the listed parameters.
* @author NIST
* @author Mary Brady
* @see <a href="http://www.w3.org/TR/DOM-Level-2-Core/core#Level-2-Core-DOM-createDocument">http://www.w3.org/TR/DOM-Level-2-Core/core#Level-2-Core-DOM-createDocument</a>
*/
public final class createDocument07 extends DOMTestCase {

   /**
    * Constructor.
    * @param factory document factory, may not be null
    * @throws org.w3c.domts.DOMTestIncompatibleException Thrown if test is not compatible with parser configuration
    */
   public createDocument07(final DOMTestDocumentBuilderFactory factory)  throws org.w3c.domts.DOMTestIncompatibleException {
      super(factory);

    //
    //   check if loaded documents are supported for content type
    //
    String contentType = getContentType();
    preload(contentType, "staffNS", false);
    }

   /**
    * Runs the test case.
    * @throws Throwable Any uncaught exception causes test to fail
    */
   public void runTest() throws Throwable {
      String namespaceURI = "http://www.ecommerce.org/schema";
      String qualifiedName = "y:x";
      Document doc;
      DocumentType docType = null;

      DOMImplementation domImpl;
      Document aNewDoc;
      String nodeName;
      String nodeValue;
      doc = (Document) load("staffNS", false);
      domImpl = doc.getImplementation();
      aNewDoc = domImpl.createDocument(namespaceURI, qualifiedName, docType);
      nodeName = aNewDoc.getNodeName();
      nodeValue = aNewDoc.getNodeValue();
      assertEquals("nodeName", "#document", nodeName);
      assertNull("nodeValue", nodeValue);
      }
   /**
    *  Gets URI that identifies the test.
    *  @return uri identifier of test
    */
   public String getTargetURI() {
      return "http://www.w3.org/2001/DOM-Test-Suite/level2/core/createDocument07";
   }
   /**
    * Runs this test from the command line.
    * @param args command line arguments
    */
   public static void main(final String[] args) {
        DOMTestCase.doMain(createDocument07.class, args);
   }
}

