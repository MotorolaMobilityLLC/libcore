/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package libcore.java.net;

import junit.framework.TestCase;

import libcore.net.MimeUtils;

public class MimeUtilsTest extends TestCase {
  public void test_15715370() {
    assertEquals("audio/flac", MimeUtils.guessMimeTypeFromExtension("flac"));
    assertEquals("flac", MimeUtils.guessExtensionFromMimeType("audio/flac"));
    assertEquals("flac", MimeUtils.guessExtensionFromMimeType("application/x-flac"));
  }

  public void testCommon() {
    assertEquals("audio/mpeg", MimeUtils.guessMimeTypeFromExtension("mp3"));
    assertEquals("image/png", MimeUtils.guessMimeTypeFromExtension("png"));
    assertEquals("application/zip", MimeUtils.guessMimeTypeFromExtension("zip"));

    assertEquals("mp3", MimeUtils.guessExtensionFromMimeType("audio/mpeg"));
    assertEquals("png", MimeUtils.guessExtensionFromMimeType("image/png"));
    assertEquals("zip", MimeUtils.guessExtensionFromMimeType("application/zip"));
  }
}
