
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
 *     If the node to be added by the "setNamedItem(arg)" method 
 *    already exists in the NamedNodeMap, it is replaced by
 *    the new one.
 *    
 *    Retrieve the second employee and create a NamedNodeMap 
 *    object from the attributes of the last child by
 *    invoking the "getAttributes()" method.  Once the
 *    list is created an invocation of the "setNamedItem(arg)"
 *    method is done with arg=newAttr, where newAttr is a
 *    new Attr Node previously created and whose node name
 *    already exists in the map.  The "setNamedItem(arg)"
 *    method should replace the already existing node with
 *    the new one.   
 *    This node is then retrieved using the "getNamedItem(name)"
 *    method.  This test uses the "createAttribute(name)"
 *    method from the document interface
* @author NIST
* @author Mary Brady
* @see <a href="http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core#ID-1025163788">http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core#ID-1025163788</a>
* @see <a href="http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core#ID-349467F9">http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core#ID-349467F9</a>
*/
public final class namednodemapsetnameditemthatexists extends DOMTestCase {

   /**
    * Constructor.
    * @param factory document factory, may not be null
    * @throws org.w3c.domts.DOMTestIncompatibleException Thrown if test is not compatible with parser configuration
    */
   public namednodemapsetnameditemthatexists(final DOMTestDocumentBuilderFactory factory)  throws org.w3c.domts.DOMTestIncompatibleException {
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
      Attr newAttribute;
      Node testAddress;
      NamedNodeMap attributes;
      Attr districtNode;
      String attrValue;
      Node setNode;
      doc = (Document) load("staff", true);
      elementList = doc.getElementsByTagName("address");
      testAddress = elementList.item(1);
      newAttribute = doc.createAttribute("street");
      attributes = testAddress.getAttributes();
      setNode = attributes.setNamedItem(newAttribute);
      districtNode = (Attr) attributes.getNamedItem("street");
      attrValue = districtNode.getNodeValue();
      assertEquals("streetValue", "", attrValue);
      }
   /**
    *  Gets URI that identifies the test.
    *  @return uri identifier of test
    */
   public String getTargetURI() {
      return "http://www.w3.org/2001/DOM-Test-Suite/level1/core/namednodemapsetnameditemthatexists";
   }
   /**
    * Runs this test from the command line.
    * @param args command line arguments
    */
   public static void main(final String[] args) {
        DOMTestCase.doMain(namednodemapsetnameditemthatexists.class, args);
   }
}

