
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
 *       Returns a NodeList of all the Elements with a given local name and namespace URI in the 
 *       order in which they are encountered in a preorder traversal of the Document tree. 
 *       Create a new element node ('root') and append three newly created child nodes (all have 
 *       local name 'child' and defined in different namespaces). 
 *       Test 1: invoke getElementsByTagNameNS to retrieve one of the children.
 *       Test 2: invoke getElementsByTagNameNS with the value of namespace equals to '*', and 
 *       verify that the node list has length of 3. 
 *     
* @author IBM
* @author Neil Delima
* @see <a href="http://www.w3.org/TR/DOM-Level-2-Core/core#ID-getElBTNNS">http://www.w3.org/TR/DOM-Level-2-Core/core#ID-getElBTNNS</a>
* @see <a href="http://www.w3.org/Bugs/Public/show_bug.cgi?id=259">http://www.w3.org/Bugs/Public/show_bug.cgi?id=259</a>
*/
public final class elementgetelementsbytagnamens04 extends DOMTestCase {

   /**
    * Constructor.
    * @param factory document factory, may not be null
    * @throws org.w3c.domts.DOMTestIncompatibleException Thrown if test is not compatible with parser configuration
    */
   public elementgetelementsbytagnamens04(final DOMTestDocumentBuilderFactory factory)  throws org.w3c.domts.DOMTestIncompatibleException {

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
      Element element;
      Element child1;
      Element child2;
      Element child3;
      Node appendedChild;
      NodeList elementList;
      String nullNS = null;

      doc = (Document) load("staffNS", false);
      element = doc.createElementNS("http://www.w3.org/DOM", "root");
      child1 = doc.createElementNS("http://www.w3.org/DOM/Level1", "dom:child");
      child2 = doc.createElementNS(nullNS, "child");
      child3 = doc.createElementNS("http://www.w3.org/DOM/Level2", "dom:child");
      appendedChild = element.appendChild(child1);
      appendedChild = element.appendChild(child2);
      appendedChild = element.appendChild(child3);
      elementList = element.getElementsByTagNameNS(nullNS, "child");
      assertSize("elementgetelementsbytagnamens04_1", 1, elementList);
      elementList = element.getElementsByTagNameNS("*", "child");
      assertSize("elementgetelementsbytagnamens04_2", 3, elementList);
      }
   /**
    *  Gets URI that identifies the test.
    *  @return uri identifier of test
    */
   public String getTargetURI() {
      return "http://www.w3.org/2001/DOM-Test-Suite/level2/core/elementgetelementsbytagnamens04";
   }
   /**
    * Runs this test from the command line.
    * @param args command line arguments
    */
   public static void main(final String[] args) {
        DOMTestCase.doMain(elementgetelementsbytagnamens04.class, args);
   }
}

