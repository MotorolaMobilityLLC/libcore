
/*
This Java source file was generated by test-to-java.xsl
and is a derived work from the source document.
The source document contained the following notice:


Copyright (c) 2001 World Wide Web Consortium,
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
 *     The "getElementsByTagName(tagName)" method returns a 
 *    NodeList of all the Elements with a given tagName
 *    in a pre-order traversal of the tree.
 *    
 *    Retrieve the entire DOM document and invoke its 
 *    "getElementsByTagName(tagName)" method with tagName
 *    equal to "strong".  The method should return a NodeList 
 *    that contains 5 elements.  The FOURTH item in the
 *    list is retrieved and output.
* @author Curt Arnold
* @see <a href="http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core#ID-A6C9094">http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core#ID-A6C9094</a>
*/
public final class hc_documentgetelementsbytagnamevalue extends DOMTestCase {

   /**
    * Constructor.
    * @param factory document factory, may not be null
    * @throws org.w3c.domts.DOMTestIncompatibleException Thrown if test is not compatible with parser configuration
    */
   public hc_documentgetelementsbytagnamevalue(final DOMTestDocumentBuilderFactory factory)  throws org.w3c.domts.DOMTestIncompatibleException {
      super(factory);

    //
    //   check if loaded documents are supported for content type
    //
    String contentType = getContentType();
    preload(contentType, "hc_staff", false);
    }

   /**
    * Runs the test case.
    * @throws Throwable Any uncaught exception causes test to fail
    */
   public void runTest() throws Throwable {
      Document doc;
      NodeList nameList;
      Node nameNode;
      Node firstChild;
      String childValue;
      doc = (Document) load("hc_staff", false);
      nameList = doc.getElementsByTagName("strong");
      nameNode = nameList.item(3);
      firstChild = nameNode.getFirstChild();
      childValue = firstChild.getNodeValue();
      assertEquals("documentGetElementsByTagNameValueAssert", "Jeny Oconnor", childValue);
      }
   /**
    *  Gets URI that identifies the test.
    *  @return uri identifier of test
    */
   public String getTargetURI() {
      return "http://www.w3.org/2001/DOM-Test-Suite/level1/core/hc_documentgetelementsbytagnamevalue";
   }
   /**
    * Runs this test from the command line.
    * @param args command line arguments
    */
   public static void main(final String[] args) {
        DOMTestCase.doMain(hc_documentgetelementsbytagnamevalue.class, args);
   }
}

