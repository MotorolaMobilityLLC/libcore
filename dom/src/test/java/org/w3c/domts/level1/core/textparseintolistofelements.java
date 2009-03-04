
/*
This Java source file was generated by test-to-java.xsl
and is a derived work from the source document.
The source document contained the following notice:


Copyright (c) 2001-2004 World Wide Web Consortium,
(Massachusetts Institute of Technology, Institut National de
Recherche en Informatique et en Automatique, Keio University). All
Rights Reserved. This program is distributed under the W3C's Software
Intellectual Property License. This program is distributed in the
hope that it will be useful, but WITHOUT ANY WARRANTY; without even
the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE.
See W3C License http://www.w3.org/Consortium/Legal/ for more details.

*/

package org.w3c.domts.level1.core;

import org.w3c.dom.*;


import org.w3c.domts.DOMTestCase;
import org.w3c.domts.DOMTestDocumentBuilderFactory;



/**
 *     Retrieve the textual data from the last child of the 
 *     second employee.   That node is composed of two   
 *     EntityReference nodes and two Text nodes.   After
 *     the content node is parsed, the "address" Element
 *     should contain four children with each one of the
 *     EntityReferences containing one child.
* @author NIST
* @author Mary Brady
* @see <a href="http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core#ID-1451460987">http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core#ID-1451460987</a>
* @see <a href="http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core#ID-11C98490">http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core#ID-11C98490</a>
* @see <a href="http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core#ID-745549614">http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core#ID-745549614</a>
*/
public final class textparseintolistofelements extends DOMTestCase {

   /**
    * Constructor.
    * @param factory document factory, may not be null
    * @throws org.w3c.domts.DOMTestIncompatibleException Thrown if test is not compatible with parser configuration
    */
   public textparseintolistofelements(final DOMTestDocumentBuilderFactory factory)  throws org.w3c.domts.DOMTestIncompatibleException {
      super(factory);

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
      NodeList elementList;
      Node addressNode;
      NodeList childList;
      Node child;
      int length;
      String value;
      Node grandChild;
      java.util.List result = new java.util.ArrayList();
      
      java.util.List expectedNormal = new java.util.ArrayList();
      expectedNormal.add("1900 Dallas Road");
      expectedNormal.add(" Dallas, ");
      expectedNormal.add("Texas");
      expectedNormal.add("\n 98554");
      
      java.util.List expectedExpanded = new java.util.ArrayList();
      expectedExpanded.add("1900 Dallas Road Dallas, Texas\n 98554");
      
      doc = (Document) load("staff", false);
      elementList = doc.getElementsByTagName("address");
      addressNode = elementList.item(1);
      childList = addressNode.getChildNodes();
      length = (int) childList.getLength();
      for (int indexN1007F = 0; indexN1007F < childList.getLength(); indexN1007F++) {
          child = (Node) childList.item(indexN1007F);
    value = child.getNodeValue();
      
      if ((value == null)) {
          grandChild = child.getFirstChild();
      assertNotNull("grandChildNotNull", grandChild);
      value = grandChild.getNodeValue();
      result.add(value);
      } else {
          result.add(value);
      }
        
      }
      
      if (equals(4, length)) {
          assertEquals("assertEqNormal", expectedNormal, result);
      } else {
          assertEquals("assertEqCoalescing", expectedExpanded, result);
      }
        
    }
   /**
    *  Gets URI that identifies the test.
    *  @return uri identifier of test
    */
   public String getTargetURI() {
      return "http://www.w3.org/2001/DOM-Test-Suite/level1/core/textparseintolistofelements";
   }
   /**
    * Runs this test from the command line.
    * @param args command line arguments
    */
   public static void main(final String[] args) {
        DOMTestCase.doMain(textparseintolistofelements.class, args);
   }
}

