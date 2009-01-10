
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
 *     The "getSpecified()" method for an Attr node should return true if the 
 *   value of the attribute is changed. 
 *   Retrieve the attribute named "class" from the last 
 *   child of of the THIRD employee and change its 
 *   value to "Yes"(which is the default DTD value).  This
 *   should cause the "getSpecified()" method to be true.
 *   This test uses the "setAttribute(name,value)" method
 *   from the Element interface and the "getNamedItem(name)"
 *   method from the NamedNodeMap interface.
* @author Curt Arnold
* @see <a href="http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core#ID-862529273">http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core#ID-862529273</a>
*/
public final class hc_attrspecifiedvaluechanged extends DOMTestCase {

   /**
    * Constructor.
    * @param factory document factory, may not be null
    * @throws org.w3c.domts.DOMTestIncompatibleException Thrown if test is not compatible with parser configuration
    */
   public hc_attrspecifiedvaluechanged(final DOMTestDocumentBuilderFactory factory)  throws org.w3c.domts.DOMTestIncompatibleException {
      super(factory);

    //
    //   check if loaded documents are supported for content type
    //
    String contentType = getContentType();
    preload(contentType, "hc_staff", true);
    }

   /**
    * Runs the test case.
    * @throws Throwable Any uncaught exception causes test to fail
    */
   public void runTest() throws Throwable {
      Document doc;
      NodeList addressList;
      Node testNode;
      NamedNodeMap attributes;
      Attr streetAttr;
      boolean state;
      doc = (Document) load("hc_staff", true);
      addressList = doc.getElementsByTagName("acronym");
      testNode = addressList.item(2);
      ((Element) /*Node */testNode).setAttribute("class", "Yα");
      attributes = testNode.getAttributes();
      streetAttr = (Attr) attributes.getNamedItem("class");
      state = streetAttr.getSpecified();
      assertTrue("acronymClassSpecified", state);
      }
   /**
    *  Gets URI that identifies the test.
    *  @return uri identifier of test
    */
   public String getTargetURI() {
      return "http://www.w3.org/2001/DOM-Test-Suite/level1/core/hc_attrspecifiedvaluechanged";
   }
   /**
    * Runs this test from the command line.
    * @param args command line arguments
    */
   public static void main(final String[] args) {
        DOMTestCase.doMain(hc_attrspecifiedvaluechanged.class, args);
   }
}

