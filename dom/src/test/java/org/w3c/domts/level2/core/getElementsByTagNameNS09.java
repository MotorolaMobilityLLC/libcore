
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
 *     The "getElementsByTagNameNS(namespaceURI,localName)" method for a 
 *    Element should return a new NodeList of all descendant Elements with a given
 *    localName and namespaceURI in the order they were encountered in a preorder
 *    traversal of the document tree.
 *    
 *    Invoke method getElementsByTagNameNS(namespaceURI,localName) on the document
 *    element with namespaceURI being "*" and localName is "employee".
 *    Method should return a new NodeList containing five Elements. 
 *    Retrieve the FOURTH element whose name should be "emp:employee".
 *    Derived from getElementsByTagNameNS02 and reflects its interpretation
 *    that namespace="*" matches namespace unqualified tagnames.
* @author Curt Arnold
* @author Curt Arnold
* @see <a href="http://www.w3.org/TR/DOM-Level-2-Core/core#ID-1938918D">http://www.w3.org/TR/DOM-Level-2-Core/core#ID-1938918D</a>
*/
public final class getElementsByTagNameNS09 extends DOMTestCase {

   /**
    * Constructor.
    * @param factory document factory, may not be null
    * @throws org.w3c.domts.DOMTestIncompatibleException Thrown if test is not compatible with parser configuration
    */
   public getElementsByTagNameNS09(final DOMTestDocumentBuilderFactory factory)  throws org.w3c.domts.DOMTestIncompatibleException {

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
    preload(contentType, "staffNS", false);
    }

   /**
    * Runs the test case.
    * @throws Throwable Any uncaught exception causes test to fail
    */
   public void runTest() throws Throwable {
      Document doc;
      NodeList newList;
      Element newElement;
      String prefix;
      String lname;
      Element docElem;
      doc = (Document) load("staffNS", false);
      docElem = doc.getDocumentElement();
      newList = docElem.getElementsByTagNameNS("*", "employee");
      assertSize("employeeCount", 5, newList);
      newElement = (Element) newList.item(3);
      prefix = newElement.getPrefix();
      assertEquals("prefix", "emp", prefix);
      lname = newElement.getLocalName();
      assertEquals("lname", "employee", lname);
      }
   /**
    *  Gets URI that identifies the test.
    *  @return uri identifier of test
    */
   public String getTargetURI() {
      return "http://www.w3.org/2001/DOM-Test-Suite/level2/core/getElementsByTagNameNS09";
   }
   /**
    * Runs this test from the command line.
    * @param args command line arguments
    */
   public static void main(final String[] args) {
        DOMTestCase.doMain(getElementsByTagNameNS09.class, args);
   }
}

