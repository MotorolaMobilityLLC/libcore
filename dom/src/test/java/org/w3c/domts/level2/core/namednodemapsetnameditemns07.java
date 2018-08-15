
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
 *  The method setNamedItemNS adds a node using its namespaceURI and localName and 
 *  raises a INUSE_ATTRIBUTE_ERR Raised if arg is an Attr that is already an 
 *  attribute of another Element object. 
 *  
 *  Retreieve the attributes of first element whose localName is address into a named node map.
 *  Retreive the attribute whose namespaceURI=http://www.usa.com and localName=domestic
 *  from the NamedNodeMap.  Retreieve the attributes of second element whose localName is address 
 *  into a named node map.  Call the setNamedItemNS method on the second nodemap with the domestic 
 *  attribute that was retreived and removed from the first nodeMap as an argument.
 *  Assuming that when an attribute is removed from a nodemap, it still remains in the domtree
 *  his should raise an INUSE_ATTRIBIUTE_ERR.
* @author IBM
* @author Neil Delima
* @see <a href="http://www.w3.org/TR/DOM-Level-2-Core/core#ID-setNamedItemNS">http://www.w3.org/TR/DOM-Level-2-Core/core#ID-setNamedItemNS</a>
*/
public final class namednodemapsetnameditemns07 extends DOMTestCase {

   /**
    * Constructor.
    * @param factory document factory, may not be null
    * @throws org.w3c.domts.DOMTestIncompatibleException Thrown if test is not compatible with parser configuration
    */
   public namednodemapsetnameditemns07(final DOMTestDocumentBuilderFactory factory)  throws org.w3c.domts.DOMTestIncompatibleException {

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
      NamedNodeMap attributes;
      NodeList elementList;
      Element element;
      Attr attr;
      Node newNode;
      doc = (Document) load("staffNS", true);
      elementList = doc.getElementsByTagNameNS("*", "address");
      element = (Element) elementList.item(0);
      attributes = element.getAttributes();
      attr = (Attr) attributes.getNamedItemNS("http://www.usa.com", "domestic");
      element = (Element) elementList.item(1);
      attributes = element.getAttributes();
      
      {
         boolean success = false;
         try {
            newNode = attributes.setNamedItemNS(attr);
          } catch (DOMException ex) {
            success = (ex.code == DOMException.INUSE_ATTRIBUTE_ERR);
         }
         assertTrue("namednodemapsetnameditemns07", success);
      }
}
   /**
    *  Gets URI that identifies the test.
    *  @return uri identifier of test
    */
   public String getTargetURI() {
      return "http://www.w3.org/2001/DOM-Test-Suite/level2/core/namednodemapsetnameditemns07";
   }
   /**
    * Runs this test from the command line.
    * @param args command line arguments
    */
   public static void main(final String[] args) {
        DOMTestCase.doMain(namednodemapsetnameditemns07.class, args);
   }
}

