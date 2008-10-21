
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

package org.w3c.dom;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

/**
 *     The method getInternalSubset() returns the public identifier of the external subset.
 *   
 *     Create a new DocumentType node with the value "SYS" for its systemId and PUB for
 *     its publicId.  Check the value of the systemId and pbulicId attributes.
* @author IBM
* @author Neil Delima
* @see <a href="http://www.w3.org/TR/DOM-Level-2-Core/core#ID-Core-DocType-systemId">http://www.w3.org/TR/DOM-Level-2-Core/core#ID-Core-DocType-systemId</a>
*/
public final class DocumentTypeSystemId extends DOMTestCase {
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
   public void testGetSystemId() throws Throwable {
      Document doc;
      DocumentType docType;
      DOMImplementation domImpl;
      String publicId;
      String systemId;
      doc = (Document) load("staffNS", builder);
      domImpl = doc.getImplementation();
      docType = domImpl.createDocumentType("l2:root", "PUB", "SYS");
      publicId = docType.getPublicId();
      systemId = docType.getSystemId();
      assertEquals("documenttypepublicid01", "PUB", publicId);
      assertEquals("documenttypesystemid01", "SYS", systemId);
      }
   
}

