
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
 *    Retrieve the entire DOM document and invoke its 
 *    "getElementsByTagName(tagName)" method with tagName
 *    equal to "*".  The method should return a NodeList 
 *    that contains all the elements of the document. 
* @author Curt Arnold
* @see <a href="http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core#ID-A6C9094">http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core#ID-A6C9094</a>
* @see <a href="http://www.w3.org/Bugs/Public/show_bug.cgi?id=251">http://www.w3.org/Bugs/Public/show_bug.cgi?id=251</a>
*/
public final class hc_documentgetelementsbytagnametotallength extends DOMTestCase {

   /**
    * Constructor.
    * @param factory document factory, may not be null
    * @throws org.w3c.domts.DOMTestIncompatibleException Thrown if test is not compatible with parser configuration
    */
   public hc_documentgetelementsbytagnametotallength(final DOMTestDocumentBuilderFactory factory)  throws org.w3c.domts.DOMTestIncompatibleException {
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
      java.util.List expectedNames = new java.util.ArrayList();
      expectedNames.add("html");
      expectedNames.add("head");
      expectedNames.add("meta");
      expectedNames.add("title");
      expectedNames.add("script");
      expectedNames.add("script");
      expectedNames.add("script");
      expectedNames.add("body");
      expectedNames.add("p");
      expectedNames.add("em");
      expectedNames.add("strong");
      expectedNames.add("code");
      expectedNames.add("sup");
      expectedNames.add("var");
      expectedNames.add("acronym");
      expectedNames.add("p");
      expectedNames.add("em");
      expectedNames.add("strong");
      expectedNames.add("code");
      expectedNames.add("sup");
      expectedNames.add("var");
      expectedNames.add("acronym");
      expectedNames.add("p");
      expectedNames.add("em");
      expectedNames.add("strong");
      expectedNames.add("code");
      expectedNames.add("sup");
      expectedNames.add("var");
      expectedNames.add("acronym");
      expectedNames.add("p");
      expectedNames.add("em");
      expectedNames.add("strong");
      expectedNames.add("code");
      expectedNames.add("sup");
      expectedNames.add("var");
      expectedNames.add("acronym");
      expectedNames.add("p");
      expectedNames.add("em");
      expectedNames.add("strong");
      expectedNames.add("code");
      expectedNames.add("sup");
      expectedNames.add("var");
      expectedNames.add("acronym");
      
      java.util.List svgExpectedNames = new java.util.ArrayList();
      svgExpectedNames.add("svg");
      svgExpectedNames.add("rect");
      svgExpectedNames.add("script");
      svgExpectedNames.add("head");
      svgExpectedNames.add("meta");
      svgExpectedNames.add("title");
      svgExpectedNames.add("body");
      svgExpectedNames.add("p");
      svgExpectedNames.add("em");
      svgExpectedNames.add("strong");
      svgExpectedNames.add("code");
      svgExpectedNames.add("sup");
      svgExpectedNames.add("var");
      svgExpectedNames.add("acronym");
      svgExpectedNames.add("p");
      svgExpectedNames.add("em");
      svgExpectedNames.add("strong");
      svgExpectedNames.add("code");
      svgExpectedNames.add("sup");
      svgExpectedNames.add("var");
      svgExpectedNames.add("acronym");
      svgExpectedNames.add("p");
      svgExpectedNames.add("em");
      svgExpectedNames.add("strong");
      svgExpectedNames.add("code");
      svgExpectedNames.add("sup");
      svgExpectedNames.add("var");
      svgExpectedNames.add("acronym");
      svgExpectedNames.add("p");
      svgExpectedNames.add("em");
      svgExpectedNames.add("strong");
      svgExpectedNames.add("code");
      svgExpectedNames.add("sup");
      svgExpectedNames.add("var");
      svgExpectedNames.add("acronym");
      svgExpectedNames.add("p");
      svgExpectedNames.add("em");
      svgExpectedNames.add("strong");
      svgExpectedNames.add("code");
      svgExpectedNames.add("sup");
      svgExpectedNames.add("var");
      svgExpectedNames.add("acronym");
      
      java.util.List actualNames = new java.util.ArrayList();
      
      Element thisElement;
      String thisTag;
      doc = (Document) load("hc_staff", false);
      nameList = doc.getElementsByTagName("*");
      for (int indexN10148 = 0; indexN10148 < nameList.getLength(); indexN10148++) {
          thisElement = (Element) nameList.item(indexN10148);
    thisTag = thisElement.getTagName();
      actualNames.add(thisTag);
        }
      
      if (("image/svg+xml".equals(getContentType()))) {
          assertEqualsAutoCase("element", "svgTagNames", svgExpectedNames, actualNames);
        } else {
          assertEqualsAutoCase("element", "tagNames", expectedNames, actualNames);
        }
        
    }
   /**
    *  Gets URI that identifies the test.
    *  @return uri identifier of test
    */
   public String getTargetURI() {
      return "http://www.w3.org/2001/DOM-Test-Suite/level1/core/hc_documentgetelementsbytagnametotallength";
   }
   /**
    * Runs this test from the command line.
    * @param args command line arguments
    */
   public static void main(final String[] args) {
        DOMTestCase.doMain(hc_documentgetelementsbytagnametotallength.class, args);
   }
}

