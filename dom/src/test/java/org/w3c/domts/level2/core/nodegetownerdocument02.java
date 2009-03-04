
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

package org.w3c.domts.level2.core;

import org.w3c.dom.*;


import org.w3c.domts.DOMTestCase;
import org.w3c.domts.DOMTestDocumentBuilderFactory;



/**
 *  The method getOwnerDocument returns the Document object associated with this node
 *   
 *  Create a new Document node.  Since this node is not used with any Document yet
 *  verify if the ownerDocument is null.  Create a new element Node on the new Document
 *  object.  Check the ownerDocument of the new element node.
* @author IBM
* @author Neil Delima
* @see <a href="http://www.w3.org/TR/DOM-Level-2-Core/core#node-ownerDoc">http://www.w3.org/TR/DOM-Level-2-Core/core#node-ownerDoc</a>
* @see <a href="http://www.w3.org/Bugs/Public/show_bug.cgi?id=259">http://www.w3.org/Bugs/Public/show_bug.cgi?id=259</a>
*/
public final class nodegetownerdocument02 extends DOMTestCase {

   /**
    * Constructor.
    * @param factory document factory, may not be null
    * @throws org.w3c.domts.DOMTestIncompatibleException Thrown if test is not compatible with parser configuration
    */
   public nodegetownerdocument02(final DOMTestDocumentBuilderFactory factory)  throws org.w3c.domts.DOMTestIncompatibleException {

      org.w3c.domts.DocumentBuilderSetting[] settings = 
          new org.w3c.domts.DocumentBuilderSetting[] {
org.w3c.domts.DocumentBuilderSetting.namespaceAware
        };
        DOMTestDocumentBuilderFactory testFactory = factory.newInstance(settings);
        setFactory(testFactory);

    //
    //   check if loaded documents are supported for content type
    //
    String contentType = getContentType();
    preload(contentType, "staff", false);
    }

   /**
    * Runs the test case.
    * @throws Throwable Any uncaught exception causes test to fail
    */
   public void runTest() throws Throwable {
      Document doc;
      Document newDoc;
      Element newElem;
      Document ownerDocDoc;
      Document ownerDocElem;
      DOMImplementation domImpl;
      DocumentType docType;
      String nullNS = null;

      doc = (Document) load("staff", false);
      domImpl = doc.getImplementation();
      docType = domImpl.createDocumentType("mydoc", nullNS, nullNS);
      newDoc = domImpl.createDocument("http://www.w3.org/DOM/Test", "mydoc", docType);
      ownerDocDoc = newDoc.getOwnerDocument();
      assertNull("nodegetownerdocument02_1", ownerDocDoc);
      newElem = newDoc.createElementNS("http://www.w3.org/DOM/Test", "myelem");
      ownerDocElem = newElem.getOwnerDocument();
      assertNotNull("nodegetownerdocument02_2", ownerDocElem);
      }
   /**
    *  Gets URI that identifies the test.
    *  @return uri identifier of test
    */
   public String getTargetURI() {
      return "http://www.w3.org/2001/DOM-Test-Suite/level2/core/nodegetownerdocument02";
   }
   /**
    * Runs this test from the command line.
    * @param args command line arguments
    */
   public static void main(final String[] args) {
        DOMTestCase.doMain(nodegetownerdocument02.class, args);
   }
}

