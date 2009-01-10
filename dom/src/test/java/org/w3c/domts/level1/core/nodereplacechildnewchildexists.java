
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
 *     Retrieve the second employee and replace its TWELFTH 
 *     child(address) with its SECOND child(employeeId).   After the
 *     replacement the second child should now be the one that used   
 *     to be at the third position and the TWELFTH child should be the
 *     one that used to be at the SECOND position.
* @author NIST
* @author Mary Brady
* @see <a href="http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core#ID-785887307">http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core#ID-785887307</a>
*/
public final class nodereplacechildnewchildexists extends DOMTestCase {

   /**
    * Constructor.
    * @param factory document factory, may not be null
    * @throws org.w3c.domts.DOMTestIncompatibleException Thrown if test is not compatible with parser configuration
    */
   public nodereplacechildnewchildexists(final DOMTestDocumentBuilderFactory factory)  throws org.w3c.domts.DOMTestIncompatibleException {
      super(factory);

    //
    //   check if loaded documents are supported for content type
    //
    String contentType = getContentType();
    preload(contentType, "staff", true);
    }

   /**
    * Runs the test case.
    * @throws Throwable Any uncaught exception causes test to fail
    */
   public void runTest() throws Throwable {
      Document doc;
      NodeList elementList;
      Node employeeNode;
      NodeList childList;
      Node oldChild = null;

      Node newChild = null;

      String childName;
      Node childNode;
      int length;
      java.util.List actual = new java.util.ArrayList();
      
      java.util.List expected = new java.util.ArrayList();
      
      java.util.List expectedWithoutWhitespace = new java.util.ArrayList();
      expectedWithoutWhitespace.add("name");
      expectedWithoutWhitespace.add("position");
      expectedWithoutWhitespace.add("salary");
      expectedWithoutWhitespace.add("gender");
      expectedWithoutWhitespace.add("employeeId");
      
      java.util.List expectedWithWhitespace = new java.util.ArrayList();
      expectedWithWhitespace.add("#text");
      expectedWithWhitespace.add("#text");
      expectedWithWhitespace.add("name");
      expectedWithWhitespace.add("#text");
      expectedWithWhitespace.add("position");
      expectedWithWhitespace.add("#text");
      expectedWithWhitespace.add("salary");
      expectedWithWhitespace.add("#text");
      expectedWithWhitespace.add("gender");
      expectedWithWhitespace.add("#text");
      expectedWithWhitespace.add("employeeId");
      expectedWithWhitespace.add("#text");
      
      Node replacedChild;
      doc = (Document) load("staff", true);
      elementList = doc.getElementsByTagName("employee");
      employeeNode = elementList.item(1);
      childList = employeeNode.getChildNodes();
      length = (int) childList.getLength();
      
      if (equals(13, length)) {
          newChild = childList.item(1);
      oldChild = childList.item(11);
      expected =  expectedWithWhitespace;
      } else {
          newChild = childList.item(0);
      oldChild = childList.item(5);
      expected =  expectedWithoutWhitespace;
      }
        
    replacedChild = employeeNode.replaceChild(newChild, oldChild);
      assertSame("return_value_same", oldChild, replacedChild);
for (int indexN100DE = 0; indexN100DE < childList.getLength(); indexN100DE++) {
          childNode = (Node) childList.item(indexN100DE);
    childName = childNode.getNodeName();
      actual.add(childName);
        }
      assertEquals("childNames", expected, actual);
      }
   /**
    *  Gets URI that identifies the test.
    *  @return uri identifier of test
    */
   public String getTargetURI() {
      return "http://www.w3.org/2001/DOM-Test-Suite/level1/core/nodereplacechildnewchildexists";
   }
   /**
    * Runs this test from the command line.
    * @param args command line arguments
    */
   public static void main(final String[] args) {
        DOMTestCase.doMain(nodereplacechildnewchildexists.class, args);
   }
}

