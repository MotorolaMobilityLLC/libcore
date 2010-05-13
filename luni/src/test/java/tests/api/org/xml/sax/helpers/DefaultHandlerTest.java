/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tests.api.org.xml.sax.helpers;

import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;

import junit.framework.TestCase;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;

import java.io.IOException;

@TestTargetClass(DefaultHandler.class)
public class DefaultHandlerTest extends TestCase {

    /*
     * Note: most of the tests have to check for an empty implementation of the
     * respective methods and, as a result, are somewhat trivial.
     */

    private DefaultHandler h = new DefaultHandler();

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "resolveEntity",
        args = { String.class, String.class }
    )
    public void testResolveEntity() {
        try {
            h.resolveEntity("publicID", "systemID");
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "notationDecl",
        args = { String.class, String.class, String.class }
    )
    public void testNotationDecl() {
        try {
            h.notationDecl("name", "publicID", "systemID");
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "unparsedEntityDecl",
        args = { String.class, String.class, String.class, String.class }
    )
    public void testUnparsedEntityDecl() {
        try {
            h.unparsedEntityDecl("name", "publicID", "systemID",
                    "notationName");
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setDocumentLocator",
        args = { org.xml.sax.Locator.class }
    )
    public void testSetDocumentLocator() {
        h.setDocumentLocator(new LocatorImpl());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "startDocument",
        args = { }
    )
    public void testStartDocument() {
        try {
            h.startDocument();
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "endDocument",
        args = { }
    )
    public void testEndDocument() {
        try {
            h.endDocument();
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "startPrefixMapping",
        args = { String.class, String.class }
    )
    public void testStartPrefixMapping() {
        try {
            h.startPrefixMapping("prefix", "uri");
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "endPrefixMapping",
        args = { String.class }
    )
    public void testEndPrefixMapping() {
        try {
            h.endPrefixMapping("prefix");
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "startElement",
        args = { String.class, String.class, String.class,
                 Attributes.class }
    )
    public void testStartElement() {
        try {
            h.startElement("uri", "name", "qname", new AttributesImpl());
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "endElement",
        args = { String.class, String.class, String.class }
    )
    public void testEndElement() {
        try {
            h.endElement("uri", "name", "qname");
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "characters",
        args = { char[].class, int.class, int.class }
    )
    public void testCharacters() {
        try {
            h.characters("The quick brown fox".toCharArray(), 4, 11);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "ignorableWhitespace",
        args = { char[].class, int.class, int.class }
    )
    public void testIgnorableWhitespace() {
        try {
            h.ignorableWhitespace("                   ".toCharArray(), 4, 11);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "processingInstruction",
        args = { String.class, String.class }
    )
    public void testProcessingInstruction() {
        try {
            h.processingInstruction("target", "data");
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "skippedEntity",
        args = { String.class }
    )
    public void testSkippedEntity() {
        try {
            h.skippedEntity("name");
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "warning",
        args = { org.xml.sax.SAXParseException.class }
    )
    public void testWarning() {
        try {
            h.warning(new SAXParseException("Foo", new LocatorImpl()));
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "error",
        args = { org.xml.sax.SAXParseException.class }
    )
    public void testError() {
        try {
            h.error(new SAXParseException("Foo", new LocatorImpl()));
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "fatalError",
        args = { org.xml.sax.SAXParseException.class }
    )
    public void testFatalError() {
        // Ordinary case
        try {
            h.fatalError(new SAXParseException("Foo", new LocatorImpl()));
            fail("SAXException expected");
        } catch (SAXException e) {
            // Expected
        }

        // No exception
        try {
            h.fatalError(null);
            fail("NullPointerException expected");
        } catch (SAXException e) {
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // Expected
        }

    }

}
