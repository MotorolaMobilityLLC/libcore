
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

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.CharacterData;

import javax.xml.parsers.DocumentBuilder;

/**
 *     The "normalize()" method puts all the nodes in the full
 *     depth of the sub-tree underneath this element into a
 *     "normal" form.
 *
 *     Retrieve the third employee and access its second child.
 *     This child contains a block of text that is spread
 *     across multiple lines.   The content of the "name" child
 *     should be parsed and treated as a single Text node.
 *     This appears to be a duplicate of elementnormalize.xml in DOM L1 Test Suite
* @author NIST
* @author Mary Brady
* @see <a href="http://www.w3.org/TR/DOM-Level-2-Core/core#ID-normalize">http://www.w3.org/TR/DOM-Level-2-Core/core#ID-normalize</a>
* @see <a href="http://www.w3.org/TR/DOM-Level-2-Core/core#ID-72AB8359">http://www.w3.org/TR/DOM-Level-2-Core/core#ID-72AB8359</a>
*/
@TestTargetClass(Element.class)
public final class Normalize extends DOMTestCase {

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
        method = "normalize",
        args = {}
    )
   public void testNormalize() throws Throwable {
      Document doc;
      Element root;
      NodeList elementList;
      Node firstChild;
      NodeList textList;
      CharacterData textNode;
      String data;
      doc = (Document) load("staff", builder);
      root = doc.getDocumentElement();
      root.normalize();
      elementList = root.getElementsByTagName("name");
      firstChild = elementList.item(2);
      textList = firstChild.getChildNodes();
      textNode = (CharacterData) textList.item(0);
      data = textNode.getData();
      assertEquals("data", "Roger\n Jones", data);
      }

}

