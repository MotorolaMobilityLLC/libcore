
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
 *     On successful invocation of the "deleteData(offset,count)"
 *    method, the "getData()" and "getLength()" methods reflect
 *    the changes. 
 *    
 *    Retrieve the character data from the last child of the
 *    first employee.  The "deleteData(offset,count)"
 *    method is then called with offset=30 and count=5.
 *    The method should delete the characters from position
 *    30 thru position 35.  The new value of the character data
 *    should be "1230 North Ave. Dallas, Texas" which is
 *    returned by the "getData()" method and "getLength()"
 *    method should return 30".
* @author Curt Arnold
* @see <a href="http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core#ID-72AB8359">http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core#ID-72AB8359</a>
* @see <a href="http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core#ID-7D61178C">http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core#ID-7D61178C</a>
* @see <a href="http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core#ID-7C603781">http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core#ID-7C603781</a>
*/
public final class hc_characterdatadeletedatagetlengthanddata extends DOMTestCase {

   /**
    * Constructor.
    * @param factory document factory, may not be null
    * @throws org.w3c.domts.DOMTestIncompatibleException Thrown if test is not compatible with parser configuration
    */
   public hc_characterdatadeletedatagetlengthanddata(final DOMTestDocumentBuilderFactory factory)  throws org.w3c.domts.DOMTestIncompatibleException {
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
      NodeList elementList;
      Node nameNode;
      CharacterData child;
      String childData;
      int childLength;
      java.util.List result = new java.util.ArrayList();
      
      doc = (Document) load("hc_staff", true);
      elementList = doc.getElementsByTagName("acronym");
      nameNode = elementList.item(0);
      child = (CharacterData) nameNode.getFirstChild();
      child.deleteData(30, 5);
      childData = child.getData();
      assertEquals("data", "1230 North Ave. Dallas, Texas ", childData);
      childLength = (int) child.getLength();
      assertEquals("length", 30, childLength);
      }
   /**
    *  Gets URI that identifies the test.
    *  @return uri identifier of test
    */
   public String getTargetURI() {
      return "http://www.w3.org/2001/DOM-Test-Suite/level1/core/hc_characterdatadeletedatagetlengthanddata";
   }
   /**
    * Runs this test from the command line.
    * @param args command line arguments
    */
   public static void main(final String[] args) {
        DOMTestCase.doMain(hc_characterdatadeletedatagetlengthanddata.class, args);
   }
}

