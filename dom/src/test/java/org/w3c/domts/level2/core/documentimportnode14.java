
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
 *  Using the method importNode with deep=true, import the fourth employee element node of this
 *  Document.  Verify if the node has been imported correctly by checking 
 *  if the default attribute present on this node has not been imported
 *  and an explicit attribute has been imported.  
* @author IBM
* @author Neil Delima
* @see <a href="http://www.w3.org/TR/DOM-Level-2-Core/core">http://www.w3.org/TR/DOM-Level-2-Core/core</a>
* @see <a href="http://www.w3.org/TR/DOM-Level-2-Core/core#Core-Document-importNode">http://www.w3.org/TR/DOM-Level-2-Core/core#Core-Document-importNode</a>
* @see <a href="http://www.w3.org/Bugs/Public/show_bug.cgi?id=259">http://www.w3.org/Bugs/Public/show_bug.cgi?id=259</a>
* @see <a href="http://www.w3.org/Bugs/Public/show_bug.cgi?id=402">http://www.w3.org/Bugs/Public/show_bug.cgi?id=402</a>
*/
public final class documentimportnode14 extends DOMTestCase {

   /**
    * Constructor.
    * @param factory document factory, may not be null
    * @throws org.w3c.domts.DOMTestIncompatibleException Thrown if test is not compatible with parser configuration
    */
   public documentimportnode14(final DOMTestDocumentBuilderFactory factory)  throws org.w3c.domts.DOMTestIncompatibleException {

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
    preload(contentType, "staffNS", true);
    }

   /**
    * Runs the test case.
    * @throws Throwable Any uncaught exception causes test to fail
    */
   public void runTest() throws Throwable {
      Document doc;
      Document newDoc;
      DOMImplementation domImpl;
      DocumentType nullDocType = null;

      NodeList childList;
      Node imported;
      Node employeeElem;
      Attr attrNode;
      String attrValue;
      String nullNS = null;

      doc = (Document) load("staffNS", true);
      childList = doc.getElementsByTagNameNS("*", "employee");
      employeeElem = childList.item(3);
      domImpl = getImplementation();
      newDoc = domImpl.createDocument(nullNS, "staff", nullDocType);
      imported = newDoc.importNode(employeeElem, true);
      attrNode = ((Element) /*Node */imported).getAttributeNodeNS(nullNS, "defaultAttr");
      assertNull("defaultAttrNotImported", attrNode);
      attrValue = ((Element) /*Node */imported).getAttributeNS("http://www.w3.org/2000/xmlns/", "emp");
      assertEquals("explicitAttrImported", "http://www.nist.gov", attrValue);
      }
   /**
    *  Gets URI that identifies the test.
    *  @return uri identifier of test
    */
   public String getTargetURI() {
      return "http://www.w3.org/2001/DOM-Test-Suite/level2/core/documentimportnode14";
   }
   /**
    * Runs this test from the command line.
    * @param args command line arguments
    */
   public static void main(final String[] args) {
        DOMTestCase.doMain(documentimportnode14.class, args);
   }
}

