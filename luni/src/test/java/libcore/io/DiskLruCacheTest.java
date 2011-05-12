/*
 * Copyright (C) 2011 The Android Open Source Project
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

package libcore.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import static libcore.io.DiskLruCache.JOURNAL_FILE;
import static libcore.io.DiskLruCache.MAGIC;
import static libcore.io.DiskLruCache.VERSION_1;

public final class DiskLruCacheTest extends TestCase {
    private File cacheDir;
    private File journalFile;
    private DiskLruCache cache;

    @Override public void setUp() throws Exception {
        super.setUp();
        cacheDir = new File(System.getProperty("java.io.tmpdir"), "DiskLruCacheTest");
        cacheDir.mkdir();
        journalFile = new File(cacheDir, JOURNAL_FILE);
        for (File file : cacheDir.listFiles()) {
            file.delete();
        }
        cache = DiskLruCache.open(cacheDir, 2, Integer.MAX_VALUE);
    }

    @Override protected void tearDown() throws Exception {
        cache.close();
        super.tearDown();
    }

    public void testEmptyCache() throws Exception {
        cache.close();
        assertJournalEquals();
    }

    public void testWriteAndReadEntry() throws Exception {
        DiskLruCache.Editor creator = cache.edit("k1");
        creator.set(0, "ABC");
        creator.set(1, "DE");
        assertNull(creator.getString(0));
        assertNull(creator.newInputStream(0));
        assertNull(creator.getString(1));
        assertNull(creator.newInputStream(1));
        creator.commit();

        DiskLruCache.Snapshot snapshot = cache.get("k1");
        assertEquals("ABC", snapshot.getString(0));
        assertEquals("DE", snapshot.getString(1));
    }

    public void testReadAndWriteEntryAcrossCacheOpenAndClose() throws Exception {
        DiskLruCache.Editor creator = cache.edit("k1");
        creator.set(0, "A");
        creator.set(1, "B");
        creator.commit();
        cache.close();

        cache = DiskLruCache.open(cacheDir, 2, Integer.MAX_VALUE);
        DiskLruCache.Snapshot snapshot = cache.get("k1");
        assertEquals("A", snapshot.getString(0));
        assertEquals("B", snapshot.getString(1));
        snapshot.close();
    }

    public void testJournalWithEditAndPublish() throws Exception {
        DiskLruCache.Editor creator = cache.edit("k1");
        assertJournalEquals("DIRTY k1"); // DIRTY must always be flushed
        creator.set(0, "AB");
        creator.set(1, "C");
        creator.commit();
        cache.close();
        assertJournalEquals("DIRTY k1", "CLEAN k1 2 1");
    }

    public void testRevertedNewFileIsRemoveInJournal() throws Exception {
        DiskLruCache.Editor creator = cache.edit("k1");
        assertJournalEquals("DIRTY k1"); // DIRTY must always be flushed
        creator.set(0, "AB");
        creator.set(1, "C");
        creator.abort();
        cache.close();
        assertJournalEquals("DIRTY k1", "REMOVE k1");
    }

    public void testUnterminatedEditIsRevertedOnClose() throws Exception {
        cache.edit("k1");
        cache.close();
        assertJournalEquals("DIRTY k1", "REMOVE k1");
    }

    public void testJournalDoesNotIncludeReadOfYetUnpublishedValue() throws Exception {
        DiskLruCache.Editor creator = cache.edit("k1");
        assertNull(cache.get("k1"));
        creator.set(0, "A");
        creator.set(1, "BC");
        creator.commit();
        cache.close();
        assertJournalEquals("DIRTY k1", "CLEAN k1 1 2");
    }

    public void testJournalWithEditAndPublishAndRead() throws Exception {
        DiskLruCache.Editor k1Creator = cache.edit("k1");
        k1Creator.set(0, "AB");
        k1Creator.set(1, "C");
        k1Creator.commit();
        DiskLruCache.Editor k2Creator = cache.edit("k2");
        k2Creator.set(0, "DEF");
        k2Creator.set(1, "G");
        k2Creator.commit();
        DiskLruCache.Snapshot k1Snapshot = cache.get("k1");
        k1Snapshot.close();
        cache.close();
        assertJournalEquals("DIRTY k1", "CLEAN k1 2 1",
                "DIRTY k2", "CLEAN k2 3 1",
                "READ k1");
    }

    public void testCannotOperateOnEditAfterPublish() throws Exception {
        DiskLruCache.Editor editor = cache.edit("k1");
        editor.set(0, "A");
        editor.set(1, "B");
        editor.commit();
        assertInoperable(editor);
    }

    public void testCannotOperateOnEditAfterRevert() throws Exception {
        DiskLruCache.Editor editor = cache.edit("k1");
        editor.set(0, "A");
        editor.set(1, "B");
        editor.abort();
        assertInoperable(editor);
    }

    public void testExplicitRemoveAppliedToDiskImmediately() throws Exception {
        DiskLruCache.Editor editor = cache.edit("k1");
        editor.set(0, "ABC");
        editor.set(1, "B");
        editor.commit();
        File k1 = getCleanFile("k1", 0);
        assertEquals("ABC", readFile(k1));
        cache.remove("k1");
        assertFalse(k1.exists());
    }

    /**
     * Each read sees a snapshot of the file at the time read was called.
     * This means that two reads of the same key can see different data.
     */
    public void testReadAndWriteOverlapsMaintainConsistency() throws Exception {
        DiskLruCache.Editor v1Creator = cache.edit("k1");
        v1Creator.set(0, "AAaa");
        v1Creator.set(1, "BBbb");
        v1Creator.commit();

        DiskLruCache.Snapshot snapshot1 = cache.get("k1");
        InputStream inV1 = snapshot1.newInputStream(0);
        assertEquals('A', inV1.read());
        assertEquals('A', inV1.read());

        DiskLruCache.Editor v1Updater = cache.edit("k1");
        v1Updater.set(0, "CCcc");
        v1Updater.set(1, "DDdd");
        v1Updater.commit();

        DiskLruCache.Snapshot snapshot2 = cache.get("k1");
        assertEquals("CCcc", snapshot2.getString(0));
        assertEquals("DDdd", snapshot2.getString(1));
        snapshot2.close();

        assertEquals('a', inV1.read());
        assertEquals('a', inV1.read());
        assertEquals("BBbb", snapshot1.getString(1));
        snapshot1.close();
    }

    public void testOpenWithDirtyKeyDeletesAllFilesForThatKey() throws Exception {
        cache.close();
        File cleanFile0 = getCleanFile("k1", 0);
        File cleanFile1 = getCleanFile("k1", 1);
        File dirtyFile0 = getDirtyFile("k1", 0);
        File dirtyFile1 = getDirtyFile("k1", 1);
        writeFile(cleanFile0, "A");
        writeFile(cleanFile1, "B");
        writeFile(dirtyFile0, "C");
        writeFile(dirtyFile1, "D");
        createJournal("CLEAN k1 1 1", "DIRTY   k1");
        cache = DiskLruCache.open(cacheDir, 2, Integer.MAX_VALUE);
        assertFalse(cleanFile0.exists());
        assertFalse(cleanFile1.exists());
        assertFalse(dirtyFile0.exists());
        assertFalse(dirtyFile1.exists());
        assertNull(cache.get("k1"));
    }

    public void testOpenWithInvalidVersionClearsDirectory() throws Exception {
        cache.close();
        generateSomeGarbageFiles();
        createJournalWithHeader(MAGIC, "0", "2", "");
        cache = DiskLruCache.open(cacheDir, 2, Integer.MAX_VALUE);
        assertGarbageFilesAllDeleted();
    }

    public void testOpenWithInvalidValueCountClearsDirectory() throws Exception {
        cache.close();
        generateSomeGarbageFiles();
        createJournalWithHeader(MAGIC, "1", "1", "");
        cache = DiskLruCache.open(cacheDir, 2, Integer.MAX_VALUE);
        assertGarbageFilesAllDeleted();
    }

    public void testOpenWithInvalidBlankLineClearsDirectory() throws Exception {
        cache.close();
        generateSomeGarbageFiles();
        createJournalWithHeader(MAGIC, "1", "2", "x");
        cache = DiskLruCache.open(cacheDir, 2, Integer.MAX_VALUE);
        assertGarbageFilesAllDeleted();
    }

    public void testOpenWithInvalidJournalLineClearsDirectory() throws Exception {
        cache.close();
        generateSomeGarbageFiles();
        createJournal("CLEAN k1 1 1", "BOGUS");
        cache = DiskLruCache.open(cacheDir, 2, Integer.MAX_VALUE);
        assertGarbageFilesAllDeleted();
        assertNull(cache.get("k1"));
    }

    public void testOpenWithInvalidFileSizeClearsDirectory() throws Exception {
        cache.close();
        generateSomeGarbageFiles();
        createJournal("CLEAN k1 0000x001 1");
        cache = DiskLruCache.open(cacheDir, 2, Integer.MAX_VALUE);
        assertGarbageFilesAllDeleted();
        assertNull(cache.get("k1"));
    }

    public void testOpenWithTruncatedLineDiscardsThatLine() throws Exception {
        cache.close();
        writeFile(getCleanFile("k1", 0), "A");
        writeFile(getCleanFile("k1", 1), "B");
        Writer writer = new FileWriter(journalFile);
        writer.write(MAGIC + "\n" + VERSION_1 + "\n2\n\nCLEAN k1 1 1"); // no trailing newline
        writer.close();
        cache = DiskLruCache.open(cacheDir, 2, Integer.MAX_VALUE);
        assertNull(cache.get("k1"));
    }

    public void testOpenWithTooManyFileSizesClearsDirectory() throws Exception {
        cache.close();
        generateSomeGarbageFiles();
        createJournal("CLEAN k1 1 1 1");
        cache = DiskLruCache.open(cacheDir, 2, Integer.MAX_VALUE);
        assertGarbageFilesAllDeleted();
        assertNull(cache.get("k1"));
    }

    public void testKeyWithSpaceNotPermitted() throws Exception {
        try {
            cache.edit("my key");
            fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testKeyWithNewlineNotPermitted() throws Exception {
        try {
            cache.edit("my\nkey");
            fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testKeyWithCarriageReturnNotPermitted() throws Exception {
        try {
            cache.edit("my\rkey");
            fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testNullKeyThrows() throws Exception {
        try {
            cache.edit(null);
            fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testCreateNewEntryWithTooFewValuesFails() throws Exception {
        DiskLruCache.Editor creator = cache.edit("k1");
        creator.set(1, "A");
        try {
            creator.commit();
            fail();
        } catch (IllegalStateException expected) {
        }

        assertFalse(getCleanFile("k1", 0).exists());
        assertFalse(getCleanFile("k1", 1).exists());
        assertFalse(getDirtyFile("k1", 0).exists());
        assertFalse(getDirtyFile("k1", 1).exists());
        assertNull(cache.get("k1"));

        DiskLruCache.Editor creator2 = cache.edit("k1");
        creator2.set(0, "B");
        creator2.set(1, "C");
        creator2.commit();
    }

    public void testRevertWithTooFewValues() throws Exception {
        DiskLruCache.Editor creator = cache.edit("k1");
        creator.set(1, "A");
        creator.abort();
        assertFalse(getCleanFile("k1", 0).exists());
        assertFalse(getCleanFile("k1", 1).exists());
        assertFalse(getDirtyFile("k1", 0).exists());
        assertFalse(getDirtyFile("k1", 1).exists());
        assertNull(cache.get("k1"));
    }

    public void testUpdateExistingEntryWithTooFewValuesReusesPreviousValues() throws Exception {
        DiskLruCache.Editor creator = cache.edit("k1");
        creator.set(0, "A");
        creator.set(1, "B");
        creator.commit();

        DiskLruCache.Editor updater = cache.edit("k1");
        updater.set(0, "C");
        updater.commit();

        DiskLruCache.Snapshot snapshot = cache.get("k1");
        assertEquals("C", snapshot.getString(0));
        assertEquals("B", snapshot.getString(1));
        snapshot.close();
    }

    public void testEvictOnInsert() throws Exception {
        cache.close();
        cache = DiskLruCache.open(cacheDir, 2, 10);

        set("A", "a", "aaa"); // size 4
        set("B", "bb", "bbbb"); // size 6
        assertEquals(10, cache.size());

        // cause the size to grow to 12 should evict 'A'
        set("C", "c", "c");
        cache.flush();
        assertEquals(8, cache.size());
        assertAbsent("A");
        assertValue("B", "bb", "bbbb");
        assertValue("C", "c", "c");

        // causing the size to grow to 10 should evict nothing
        set("D", "d", "d");
        cache.flush();
        assertEquals(10, cache.size());
        assertAbsent("A");
        assertValue("B", "bb", "bbbb");
        assertValue("C", "c", "c");
        assertValue("D", "d", "d");

        // causing the size to grow to 18 should evict 'B' and 'C'
        set("E", "eeee", "eeee");
        cache.flush();
        assertEquals(10, cache.size());
        assertAbsent("A");
        assertAbsent("B");
        assertAbsent("C");
        assertValue("D", "d", "d");
        assertValue("E", "eeee", "eeee");
    }

    public void testEvictOnUpdate() throws Exception {
        cache.close();
        cache = DiskLruCache.open(cacheDir, 2, 10);

        set("A", "a", "aa"); // size 3
        set("B", "b", "bb"); // size 3
        set("C", "c", "cc"); // size 3
        assertEquals(9, cache.size());

        // causing the size to grow to 11 should evict 'A'
        set("B", "b", "bbbb");
        cache.flush();
        assertEquals(8, cache.size());
        assertAbsent("A");
        assertValue("B", "b", "bbbb");
        assertValue("C", "c", "cc");
    }

    public void testEvictionHonorsLruFromCurrentSession() throws Exception {
        cache.close();
        cache = DiskLruCache.open(cacheDir, 2, 10);
        set("A", "a", "a");
        set("B", "b", "b");
        set("C", "c", "c");
        set("D", "d", "d");
        set("E", "e", "e");
        cache.get("B").close(); // 'B' is now least recently used

        // causing the size to grow to 12 should evict 'A'
        set("F", "f", "f");
        // causing the size to grow to 12 should evict 'C'
        set("G", "g", "g");
        cache.flush();
        assertEquals(10, cache.size());
        assertAbsent("A");
        assertValue("B", "b", "b");
        assertAbsent("C");
        assertValue("D", "d", "d");
        assertValue("E", "e", "e");
        assertValue("F", "f", "f");
    }

    public void testEvictionHonorsLruFromPreviousSession() throws Exception {
        set("A", "a", "a");
        set("B", "b", "b");
        set("C", "c", "c");
        set("D", "d", "d");
        set("E", "e", "e");
        set("F", "f", "f");
        cache.get("B").close(); // 'B' is now least recently used
        assertEquals(12, cache.size());
        cache.close();
        cache = DiskLruCache.open(cacheDir, 2, 10);

        set("G", "g", "g");
        cache.flush();
        assertEquals(10, cache.size());
        assertAbsent("A");
        assertValue("B", "b", "b");
        assertAbsent("C");
        assertValue("D", "d", "d");
        assertValue("E", "e", "e");
        assertValue("F", "f", "f");
        assertValue("G", "g", "g");
    }

    public void testCacheSingleEntryOfSizeGreaterThanMaxSize() throws Exception {
        cache.close();
        cache = DiskLruCache.open(cacheDir, 2, 10);
        set("A", "aaaaa", "aaaaaa"); // size=11
        cache.flush();
        assertAbsent("A");
    }

    public void testCacheSingleValueOfSizeGreaterThanMaxSize() throws Exception {
        cache.close();
        cache = DiskLruCache.open(cacheDir, 2, 10);
        set("A", "aaaaaaaaaaa", "a"); // size=12
        cache.flush();
        assertAbsent("A");
    }

    public void testConstructorDoesNotAllowZeroCacheSize() throws Exception {
        try {
            DiskLruCache.open(cacheDir, 2, 0);
            fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testConstructorDoesNotAllowZeroValuesPerEntry() throws Exception {
        try {
            DiskLruCache.open(cacheDir, 0, 10);
            fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testRemoveAbsentElement() throws Exception {
        cache.remove("A");
    }

    public void testReadingTheSameStreamMultipleTimes() throws Exception {
        set("A", "a", "b");
        DiskLruCache.Snapshot snapshot = cache.get("A");
        InputStream in1 = snapshot.newInputStream(0);
        assertEquals('a', in1.read());
        assertEquals(-1, in1.read());
        in1.close();

        InputStream in2 = snapshot.newInputStream(0);
        assertEquals('a', in2.read());
        assertEquals(-1, in2.read());
        in2.close();
    }

    private void assertJournalEquals(String... expectedBodyLines) throws Exception {
        List<String> expectedLines = new ArrayList<String>();
        expectedLines.add(MAGIC);
        expectedLines.add(VERSION_1);
        expectedLines.add("2");
        expectedLines.add("");
        expectedLines.addAll(Arrays.asList(expectedBodyLines));
        assertEquals(expectedLines, readJournalLines());
    }

    private void createJournal(String... bodyLines) throws Exception {
        createJournalWithHeader(MAGIC, VERSION_1, "2", "", bodyLines);
    }

    private void createJournalWithHeader(String magic, String version, String valueCount,
            String blank, String... bodyLines) throws Exception {
        Writer writer = new FileWriter(journalFile);
        writer.write(magic + "\n");
        writer.write(version + "\n");
        writer.write(valueCount + "\n");
        writer.write(blank + "\n");
        for (String line : bodyLines) {
            writer.write(line);
            writer.write('\n');
        }
        writer.close();
    }

    private List<String> readJournalLines() throws Exception {
        List<String> result = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(journalFile));
        String line;
        while ((line = reader.readLine()) != null) {
            result.add(line);
        }
        reader.close();
        return result;
    }

    private File getCleanFile(String key, int index) {
        return new File(cacheDir, key + "." + index);
    }

    private File getDirtyFile(String key, int index) {
        return new File(cacheDir, key + "." + index + ".tmp");
    }

    private String readFile(File file) throws Exception {
        Reader reader = new FileReader(file);
        StringWriter writer = new StringWriter();
        char[] buffer = new char[1024];
        int count;
        while ((count = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, count);
        }
        reader.close();
        return writer.toString();
    }

    public void writeFile(File file, String content) throws Exception {
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();
    }

    private void assertInoperable(DiskLruCache.Editor editor) throws Exception {
        try {
            editor.getString(0);
            fail();
        } catch (IllegalStateException expected) {
        }
        try {
            editor.set(0, "A");
            fail();
        } catch (IllegalStateException expected) {
        }
        try {
            editor.newInputStream(0);
            fail();
        } catch (IllegalStateException expected) {
        }
        try {
            editor.newOutputStream(0);
            fail();
        } catch (IllegalStateException expected) {
        }
        try {
            editor.commit();
            fail();
        } catch (IllegalStateException expected) {
        }
        try {
            editor.abort();
            fail();
        } catch (IllegalStateException expected) {
        }
    }

    private void generateSomeGarbageFiles() throws Exception {
        File dir1 = new File(cacheDir, "dir1");
        File dir2 = new File(dir1, "dir2");
        writeFile(getCleanFile("g1", 0), "A");
        writeFile(getCleanFile("g1", 1), "B");
        writeFile(getCleanFile("g2", 0), "C");
        writeFile(getCleanFile("g2", 1), "D");
        writeFile(getCleanFile("g2", 1), "D");
        writeFile(new File(cacheDir, "otherFile0"), "E");
        dir1.mkdir();
        dir2.mkdir();
        writeFile(new File(dir2, "otherFile1"), "F");
    }

    private void assertGarbageFilesAllDeleted() throws Exception {
        assertFalse(getCleanFile("g1", 0).exists());
        assertFalse(getCleanFile("g1", 1).exists());
        assertFalse(getCleanFile("g2", 0).exists());
        assertFalse(getCleanFile("g2", 1).exists());
        assertFalse(new File(cacheDir, "otherFile0").exists());
        assertFalse(new File(cacheDir, "dir1").exists());
    }

    private void set(String key, String value0, String value1) throws Exception {
        DiskLruCache.Editor editor = cache.edit(key);
        editor.set(0, value0);
        editor.set(1, value1);
        editor.commit();
    }

    private void assertAbsent(String key) throws Exception {
        DiskLruCache.Snapshot snapshot = cache.get(key);
        if (snapshot != null) {
            snapshot.close();
            fail();
        }
        assertFalse(getCleanFile(key, 0).exists());
        assertFalse(getCleanFile(key, 1).exists());
        assertFalse(getDirtyFile(key, 0).exists());
        assertFalse(getDirtyFile(key, 1).exists());
    }

    private void assertValue(String key, String value0, String value1) throws Exception {
        DiskLruCache.Snapshot snapshot = cache.get(key);
        assertEquals(value0, snapshot.getString(0));
        assertEquals(value1, snapshot.getString(1));
        assertTrue(getCleanFile(key, 0).exists());
        assertTrue(getCleanFile(key, 1).exists());
        snapshot.close();
    }
}