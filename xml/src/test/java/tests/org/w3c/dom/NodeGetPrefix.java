
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

package tests.org.w3c.dom;

import dalvik.annotation.TestTargets;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargetClass;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

import javax.xml.parsers.DocumentBuilder;

/**
 *     The method getPrefix returns the namespace prefix of this node, or null if it is unspecified.
 *   
 *   Ceate two new element nodes and atribute nodes, with and without namespace prefixes.
 *   Retreive the prefix part of their qualified names using getPrefix and verify
 *   if it is correct.
* @author IBM
* @author Neil Delima
* @see <a href="http://www.w3.org/TR/DOM-Level-2-Core/core#ID-NodeNSPrefix">http://www.w3.org/TR/DOM-Level-2-Core/core#ID-NodeNSPrefix</a>
*/
@TestTargetClass(Node.class) 
public final class NodeGetPrefix extends DOMTestCase {

    DOMDocumentBuilderFactory factory;

    DocumentBuilder builder;

    protected void setUp() throws Exception {
        super.setUp();
        try {
            factory = new DOMDocumentBuilderFactory(DOMDocumentBuilderFactory
                    .getConfiguration1());
            builder = factory.getBuilder();
        } catch (Exception e) {
            fail("Unexpected exception" + e.getMessage());
        }
    }

    protected void tearDown() throws Exception {
        factory = null;
        builder = null;
        super.tearDown();
    }
    

   /**
    * Runs the test case.
    * @throws Throwable Any uncaught exception causes test to fail
    */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "getPrefix",
        args = {}
    )
   public void testGetPrefix() throws Throwable {
      Document doc;
      Element element;
      Element qelement;
      Attr attr;
      Attr qattr;
      String elemNoPrefix;
      String elemPrefix;
      String attrNoPrefix;
      String attrPrefix;
      doc = (Document) load("staff", builder);
      element = doc.createElementNS("http://www.w3.org/DOM/Test/elem", "elem");
      qelement = doc.createElementNS("http://www.w3.org/DOM/Test/elem", "qual:qelem");
      attr = doc.createAttributeNS("http://www.w3.org/DOM/Test/attr", "attr");
      qattr = doc.createAttributeNS("http://www.w3.org/DOM/Test/attr", "qual:qattr");
      elemNoPrefix = element.getPrefix();
      elemPrefix = qelement.getPrefix();
      attrNoPrefix = attr.getPrefix();
      attrPrefix = qattr.getPrefix();
      assertNull("nodegetprefix03_1", elemNoPrefix);
      assertEquals("nodegetprefix03_2", "qual", elemPrefix);
      assertNull("nodegetprefix03_3", attrNoPrefix);
      assertEquals("nodegetprefix03_4", "qual", attrPrefix);
      }
   
}

