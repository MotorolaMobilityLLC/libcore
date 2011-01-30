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

package libcore.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;

public final class LruCacheTest extends TestCase {

    private int expectedCreateCount;
    private int expectedPutCount;
    private int expectedHitCount;
    private int expectedMissCount;
    private int expectedEvictionCount;

    public void testStatistics() {
        LruCache<String, String> cache = new LruCache<String, String>(3);
        assertStatistics(cache);

        assertEquals(null, cache.put("a", "A"));
        expectedPutCount++;
        assertStatistics(cache);
        assertHit(cache, "a", "A");
        assertSnapshot(cache, "a", "A");

        assertEquals(null, cache.put("b", "B"));
        expectedPutCount++;
        assertStatistics(cache);
        assertHit(cache, "a", "A");
        assertHit(cache, "b", "B");
        assertSnapshot(cache, "a", "A", "b", "B");

        assertEquals(null, cache.put("c", "C"));
        expectedPutCount++;
        assertStatistics(cache);
        assertHit(cache, "a", "A");
        assertHit(cache, "b", "B");
        assertHit(cache, "c", "C");
        assertSnapshot(cache, "a", "A", "b", "B", "c", "C");

        assertEquals(null, cache.put("d", "D"));
        expectedPutCount++;
        expectedEvictionCount++; // a should have been evicted
        assertStatistics(cache);
        assertMiss(cache, "a");
        assertHit(cache, "b", "B");
        assertHit(cache, "c", "C");
        assertHit(cache, "d", "D");
        assertHit(cache, "b", "B");
        assertHit(cache, "c", "C");
        assertSnapshot(cache, "d", "D", "b", "B", "c", "C");

        assertEquals(null, cache.put("e", "E"));
        expectedPutCount++;
        expectedEvictionCount++; // d should have been evicted
        assertStatistics(cache);
        assertMiss(cache, "d");
        assertMiss(cache, "a");
        assertHit(cache, "e", "E");
        assertHit(cache, "b", "B");
        assertHit(cache, "c", "C");
        assertSnapshot(cache, "e", "E", "b", "B", "c", "C");
    }

    public void testStatisticsWithCreate() {
        LruCache<String, String> cache = newCreatingCache();
        assertStatistics(cache);

        assertCreated(cache, "aa", "created-aa");
        assertHit(cache, "aa", "created-aa");
        assertSnapshot(cache, "aa", "created-aa");

        assertCreated(cache, "bb", "created-bb");
        assertMiss(cache, "c");
        assertSnapshot(cache, "aa", "created-aa", "bb", "created-bb");

        assertCreated(cache, "cc", "created-cc");
        assertSnapshot(cache, "aa", "created-aa", "bb", "created-bb", "cc", "created-cc");

        expectedEvictionCount++; // aa will be evicted
        assertCreated(cache, "dd", "created-dd");
        assertSnapshot(cache, "bb", "created-bb",  "cc", "created-cc", "dd", "created-dd");

        expectedEvictionCount++; // bb will be evicted
        assertCreated(cache, "aa", "created-aa");
        assertSnapshot(cache, "cc", "created-cc", "dd", "created-dd", "aa", "created-aa");
    }

    public void testCreateOnCacheMiss() {
        LruCache<String, String> cache = newCreatingCache();

        String created = cache.get("aa");
        assertEquals("created-aa", created);
    }

    public void testNoCreateOnCacheHit() {
        LruCache<String, String> cache = newCreatingCache();

        cache.put("aa", "put-aa");
        assertEquals("put-aa", cache.get("aa"));
    }

    public void testConstructorDoesNotAllowZeroCacheSize() {
        try {
            new LruCache<String, String>(0);
            fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testCannotPutNullKey() {
        LruCache<String, String> cache = new LruCache<String, String>(3);
        try {
            cache.put(null, "A");
            fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testCannotPutNullValue() {
        LruCache<String, String> cache = new LruCache<String, String>(3);
        try {
            cache.put("a", null);
            fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testToString() {
        LruCache<String, String> cache = new LruCache<String, String>(3);
        assertEquals("LruCache[maxSize=3,hits=0,misses=0,hitRate=0%]", cache.toString());

        cache.put("a", "A");
        cache.put("b", "B");
        cache.put("c", "C");
        cache.put("d", "D");

        cache.get("a"); // miss
        cache.get("b"); // hit
        cache.get("c"); // hit
        cache.get("d"); // hit
        cache.get("e"); // miss

        assertEquals("LruCache[maxSize=3,hits=3,misses=2,hitRate=60%]", cache.toString());
    }

    public void testEvictionWithSingletonCache() {
        LruCache<String, String> cache = new LruCache<String, String>(1);
        cache.put("a", "A");
        cache.put("b", "B");
        assertSnapshot(cache, "b", "B");
    }

    public void testEntryEvictedWhenFull() {
        List<String> expectedEvictionLog = new ArrayList<String>();
        final List<String> evictionLog = new ArrayList<String>();
        LruCache<String, String> cache = new LruCache<String, String>(3) {
            @Override protected void entryEvicted(String key, String value) {
                evictionLog.add(key + "=" + value);
            }
        };

        cache.put("a", "A");
        cache.put("b", "B");
        cache.put("c", "C");
        assertEquals(expectedEvictionLog, evictionLog);

        cache.put("d", "D");
        expectedEvictionLog.add("a=A");
        assertEquals(expectedEvictionLog, evictionLog);
    }

    /**
     * Replacing the value for a key doesn't cause an eviction but it does bring
     * the replaced entry to the front of the queue.
     */
    public void testPutDoesNotCauseEviction() {
        final List<String> evictionLog = new ArrayList<String>();
        List<String> expectedEvictionLog = new ArrayList<String>();
        LruCache<String, String> cache = new LruCache<String, String>(3) {
            @Override protected void entryEvicted(String key, String value) {
                evictionLog.add(key + "=" + value);
            }
        };

        cache.put("a", "A");
        cache.put("b", "B");
        cache.put("c", "C");
        cache.put("b", "B2");
        assertEquals(expectedEvictionLog, evictionLog);
        assertSnapshot(cache, "a", "A", "c", "C", "b", "B2");
    }

    private LruCache<String, String> newCreatingCache() {
        return new LruCache<String, String>(3) {
            @Override protected String create(String key) {
                return (key.length() > 1) ? ("created-" + key) : null;
            }
        };
    }

    private void assertHit(LruCache<String, String> cache, String key, String value) {
        assertEquals(value, cache.get(key));
        expectedHitCount++;
        assertStatistics(cache);
    }

    private void assertMiss(LruCache<String, String> cache, String key) {
        assertEquals(null, cache.get(key));
        expectedMissCount++;
        assertStatistics(cache);
    }

    private void assertCreated(LruCache<String, String> cache, String key, String value) {
        assertEquals(value, cache.get(key));
        expectedMissCount++;
        expectedCreateCount++;
        assertStatistics(cache);
    }

    private void assertStatistics(LruCache<?, ?> cache) {
        assertEquals("create count", expectedCreateCount, cache.createCount());
        assertEquals("put count", expectedPutCount, cache.putCount());
        assertEquals("hit count", expectedHitCount, cache.hitCount());
        assertEquals("miss count", expectedMissCount, cache.missCount());
        assertEquals("eviction count", expectedEvictionCount, cache.evictionCount());
    }

    private <T> void assertSnapshot(LruCache<T, T> cache, T... keysAndValues) {
        List<T> actualKeysAndValues = new ArrayList<T>();
        for (Map.Entry<T, T> entry : cache.snapshot().entrySet()) {
            actualKeysAndValues.add(entry.getKey());
            actualKeysAndValues.add(entry.getValue());
        }

        // assert using lists because order is important for LRUs
        assertEquals(Arrays.asList(keysAndValues), actualKeysAndValues);
    }
}
