
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
 *  The importNode method imports a node from another document to this document. 
 *  The returned node has no parent; (parentNode is null). The source node is not 
 *  altered or removed from the original document but a new copy of the source node
 *  is created.
 *  
 *  Using the method importNode with deep=true/false, import a entity nodes ent2 and ent6
 *  from this document to a new document object.  Verify if the nodes have been 
 *  imported correctly by checking the nodeNames of the imported nodes and public and system ids.
* @author IBM
* @author Neil Delima
* @see <a href="http://www.w3.org/TR/DOM-Level-2-Core/core">http://www.w3.org/TR/DOM-Level-2-Core/core</a>
* @see <a href="http://www.w3.org/TR/DOM-Level-2-Core/core#Core-Document-importNode">http://www.w3.org/TR/DOM-Level-2-Core/core#Core-Document-importNode</a>
*/
public final class documentimportnode19 extends DOMTestCase {

   /**
    * Constructor.
    * @param factory document factory, may not be null
    * @throws org.w3c.domts.DOMTestIncompatibleException Thrown if test is not compatible with parser configuration
    */
   public documentimportnode19(final DOMTestDocumentBuilderFactory factory)  throws org.w3c.domts.DOMTestIncompatibleException {

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
      DocumentType docTypeNull = null;

      Document docImp;
      DOMImplementation domImpl;
      DocumentType docType;
      NamedNodeMap nodeMap;
      Entity entity2;
      Entity entity6;
      Entity entityImp2;
      Entity entityImp6;
      String nodeName;
      String systemId;
      String notationName;
      String nodeNameImp;
      String systemIdImp;
      String notationNameImp;
      doc = (Document) load("staffNS", true);
      domImpl = doc.getImplementation();
      docType = doc.getDoctype();
      docImp = domImpl.createDocument("http://www.w3.org/DOM/Test", "a:b", docTypeNull);
      nodeMap = docType.getEntities();
      assertNotNull("entitiesNotNull", nodeMap);
      entity2 = (Entity) nodeMap.getNamedItem("ent2");
      entity6 = (Entity) nodeMap.getNamedItem("ent6");
      entityImp2 = (Entity) docImp.importNode(entity2, false);
      entityImp6 = (Entity) docImp.importNode(entity6, true);
      nodeName = entity2.getNodeName();
      nodeNameImp = entityImp2.getNodeName();
      assertEquals("documentimportnode19_Ent2NodeName", nodeName, nodeNameImp);
      nodeName = entity6.getNodeName();
      nodeNameImp = entityImp6.getNodeName();
      assertEquals("documentimportnode19_Ent6NodeName", nodeName, nodeNameImp);
      systemId = entity2.getSystemId();
      systemIdImp = entityImp2.getSystemId();
      assertEquals("documentimportnode19_Ent2SystemId", systemId, systemIdImp);
      systemId = entity6.getSystemId();
      systemIdImp = entityImp6.getSystemId();
      assertEquals("documentimportnode19_Ent6SystemId", systemId, systemIdImp);
      notationName = entity2.getNotationName();
      notationNameImp = entityImp2.getNotationName();
      assertEquals("documentimportnode19_Ent2NotationName", notationName, notationNameImp);
      notationName = entity6.getNotationName();
      notationNameImp = entityImp6.getNotationName();
      assertEquals("documentimportnode19_Ent6NotationName", notationName, notationNameImp);
      }
   /**
    *  Gets URI that identifies the test.
    *  @return uri identifier of test
    */
   public String getTargetURI() {
      return "http://www.w3.org/2001/DOM-Test-Suite/level2/core/documentimportnode19";
   }
   /**
    * Runs this test from the command line.
    * @param args command line arguments
    */
   public static void main(final String[] args) {
        DOMTestCase.doMain(documentimportnode19.class, args);
   }
}

