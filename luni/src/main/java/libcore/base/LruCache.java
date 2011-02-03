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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A cache that holds strong references to a limited number of values. Each time
 * a value is accessed, it is moved to the head of a queue. When a value is
 * added to a full cache, the value at the end of that queue is evicted and may
 * become eligible for garbage collection.
 *
 * <p>If your cached values hold resources and need to be explicitly released,
 * override {@link #entryEvicted}. This method is only invoked when values are
 * evicted. Values replaced by calls to {@link #put} must be released manually.
 *
 * <p>If cache values should be computed on demand for the corresponding keys,
 * override {@link #create}. This simplifies the calling code, allowing it to
 * assume a value will always be returned, even when there's a cache miss.
 *
 * <p>By default, the cache size is measured in the number of entries. Override
 * {@link #sizeOf} to size the cache in different units. For, this cache is
 * limited to 4MiB of bitmaps:
 * <pre>   {@code
 * int cacheSize = 4 * 1024 * 1024; // 4MiB
 * LruCache<String, Bitmap> bitmapCache = new LruCache<String, Bitmap>(cacheSize) {
 *     @Override protected int sizeOf(String key, Bitmap value) {
 *         return value.getByteCount();
 *     }
 * }}</pre>
 */
public class LruCache<K, V> {
    private final LinkedHashMap<K, V> map;

    /** Size of this cache in units. Not necessarily the number of elements. */
    private int size;
    private final int maxSize;

    private int putCount;
    private int createCount;
    private int evictionCount;
    private int hitCount;
    private int missCount;

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *     the maximum number of entries in the cache. For all other caches,
     *     this is the maximum sum of the sizes of the entries in this cache.
     */
    public LruCache(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        this.maxSize = maxSize;
        this.map = new LinkedHashMap<K, V>(0, 0.75f, true);
    }

    /**
     * Returns the value for {@code key} if it exists in the cache or can be
     * created by {@code #create}. If a value was returned, it is moved to the
     * head of the queue. This returns null if a value is not cached and cannot
     * be created.
     */
    public synchronized final V get(K key) {
        if (key == null) {
            throw new NullPointerException();
        }

        V result = map.get(key);
        if (result != null) {
            hitCount++;
            return result;
        }

        missCount++;

        // TODO: release the lock while calling this potentially slow user code
        result = create(key);

        if (result != null) {
            createCount++;
            size += safeSizeOf(key, result);
            map.put(key, result);
            trimToSize();
        }
        return result;
    }

    /**
     * Caches {@code value} for {@code key}. The value is moved to the head of
     * the queue.
     *
     * @return the previous value mapped by {@code key}. Although that entry is
     *     no longer cached, it has not been passed to {@link #entryEvicted}.
     */
    public synchronized final V put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException();
        }

        putCount++;
        size += safeSizeOf(key, value);
        V previous = map.put(key, value);
        if (previous != null) {
            size -= safeSizeOf(key, previous);
        }
        trimToSize();
        return previous;
    }

    private void trimToSize() {
        while (size > maxSize) {
            Map.Entry<K, V> toEvict = map.eldest();
            if (toEvict == null) {
                break; // map is empty so size should be 0! Throw an error below
            }

            K key = toEvict.getKey();
            V value = toEvict.getValue();
            map.remove(key);
            size -= safeSizeOf(key, value);
            evictionCount++;

            // TODO: release the lock while calling this potentially slow user code
            entryEvicted(key, value);
        }

        if (size < 0 || (map.isEmpty() && size != 0)) {
            throw new IllegalStateException(getClass().getName()
                    + ".sizeOf() is reporting inconsistent results!");
        }
    }

    /**
     * Called for entries that have reached the tail of the least recently used
     * queue and are be removed. The default implementation does nothing.
     */
    protected void entryEvicted(K key, V value) {}

    /**
     * Called after a cache miss to compute a value for the corresponding key.
     * Returns the computed value or null if no value can be computed. The
     * default implementation returns null.
     */
    protected V create(K key) {
        return null;
    }

    private int safeSizeOf(K key, V value) {
        int result = sizeOf(key, value);
        if (result < 0) {
            throw new IllegalStateException("Negative size: " + key + "=" + value);
        }
        return result;
    }

    /**
     * Returns the size of the entry for {@code key} and {@code value} in
     * user-defined units.  The default implementation returns 1 so that size
     * is the number of entries and max size is the maximum number of entries.
     *
     * <p>An entry's size must not change while it is in the cache.
     */
    protected int sizeOf(K key, V value) {
        return 1;
    }

    /**
     * For caches that do not override {@link #sizeOf}, this is the number of
     * entries in the cache. For all other caches, this is the sum of the sizes
     * of the entries in this cache.
     */
    public synchronized final int size() {
        return size;
    }

    /**
     * Returns the number of times {@link #get} returned a value.
     */
    public synchronized final int hitCount() {
        return hitCount;
    }

    /**
     * Returns the number of times {@link #get} returned null or required a new
     * value to be created.
     */
    public synchronized final int missCount() {
        return missCount;
    }

    /**
     * Returns the number of times {@link #create(Object)} returned a value.
     */
    public synchronized final int createCount() {
        return createCount;
    }

    /**
     * Returns the number of times {@link #put} was called.
     */
    public synchronized final int putCount() {
        return putCount;
    }

    /**
     * Returns the number of values that have been evicted.
     */
    public synchronized final int evictionCount() {
        return evictionCount;
    }

    /**
     * Returns a copy of the current contents of the cache, ordered from least
     * recently accessed to most recently accessed.
     */
    public synchronized Map<K, V> snapshot() {
        return new LinkedHashMap<K, V>(map);
    }

    @Override public synchronized final String toString() {
        int accesses = hitCount + missCount;
        int hitPercent = accesses != 0 ? (100 * hitCount / accesses) : 0;
        return String.format("LruCache[maxSize=%d,hits=%d,misses=%d,hitRate=%d%%]",
                maxSize, hitCount, missCount, hitPercent);
    }
}
