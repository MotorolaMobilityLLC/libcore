
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

package org.w3c.domts.level2.core;

import org.w3c.dom.*;


import org.w3c.domts.DOMTestCase;
import org.w3c.domts.DOMTestDocumentBuilderFactory;



/**
 *       The method setAttributeNS adds a new attribute.
 *       Retreive an existing element node with a default attribute node and 
 *       add two new attribute nodes that have the same local name as the 
 *       default attribute but different namespaceURI to it using the setAttributeNS method.   
 *       Check if the attribute was correctly set by invoking the getAttributeNodeNS method
 *       and checking the nodeName and nodeValue of the returned nodes.
 *     
* @author IBM
* @author Neil Delima
* @see <a href="http://www.w3.org/TR/DOM-Level-2-Core/core#ID-ElSetAttrNS">http://www.w3.org/TR/DOM-Level-2-Core/core#ID-ElSetAttrNS</a>
*/
public final class elementsetattributens03 extends DOMTestCase {

   /**
    * Constructor.
    * @param factory document factory, may not be null
    * @throws org.w3c.domts.DOMTestIncompatibleException Thrown if test is not compatible with parser configuration
    */
   public elementsetattributens03(final DOMTestDocumentBuilderFactory factory)  throws org.w3c.domts.DOMTestIncompatibleException {

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
      Element element;
      Attr attribute;
      NodeList elementList;
      String attrName;
      String attrValue;
      doc = (Document) load("staffNS", true);
      elementList = doc.getElementsByTagName("emp:employee");
      element = (Element) elementList.item(0);
      assertNotNull("empEmployeeNotNull", element);
      element.setAttributeNS("http://www.w3.org/DOM/Test/1", "defaultAttr", "default1");
      element.setAttributeNS("http://www.w3.org/DOM/Test/2", "defaultAttr", "default2");
      attribute = element.getAttributeNodeNS("http://www.w3.org/DOM/Test/1", "defaultAttr");
      attrName = attribute.getNodeName();
      attrValue = attribute.getNodeValue();
      assertEquals("elementsetattributens03_attrName", "defaultAttr", attrName);
      assertEquals("elementsetattributens03_attrValue", "default1", attrValue);
      }
   /**
    *  Gets URI that identifies the test.
    *  @return uri identifier of test
    */
   public String getTargetURI() {
      return "http://www.w3.org/2001/DOM-Test-Suite/level2/core/elementsetattributens03";
   }
   /**
    * Runs this test from the command line.
    * @param args command line arguments
    */
   public static void main(final String[] args) {
        DOMTestCase.doMain(elementsetattributens03.class, args);
   }
}

