/*
 * Copyright (C) 2019 The Android Open Source Project
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
 * limitations under the License
 */

package android.compat;

import android.compat.annotation.ChangeId;

import libcore.api.CorePlatformApi;
import libcore.api.IntraCoreApi;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Internal APIs for logging and gating compatibility changes.
 *
 * @see ChangeId
 *
 * @hide
 */
@CorePlatformApi(status = CorePlatformApi.Status.STABLE)
@IntraCoreApi
public final class Compatibility {

    private Compatibility() {}

    /**
     * Reports that a compatibility change is affecting the current process now.
     *
     * <p>Calls to this method from a non-app process are ignored. This allows code implementing
     * APIs that are used by apps and by other code (e.g. the system server) to report changes
     * regardless of the process it's running in. When called in a non-app process, this method is
     * a no-op.
     *
     * <p>Note: for changes that are gated using {@link #isChangeEnabled(long)}, you do not need to
     * call this API directly. The change will be reported for you in the case that
     * {@link #isChangeEnabled(long)} returns {@code true}.
     *
     * @param changeId The ID of the compatibility change taking effect.
     */
    @CorePlatformApi(status = CorePlatformApi.Status.STABLE)
    @IntraCoreApi
    public static void reportUnconditionalChange(@ChangeId long changeId) {
        sCallbacks.onChangeReported(changeId);
    }

    /**
     * Query if a given compatibility change is enabled for the current process. This method should
     * only be called by code running inside a process of the affected app.
     *
     * <p>If this method returns {@code true}, the calling code should implement the compatibility
     * change, resulting in differing behaviour compared to earlier releases. If this method returns
     * {@code false}, the calling code should behave as it did in earlier releases.
     *
     * <p>When this method returns {@code true}, it will also report the change as
     * {@link #reportUnconditionalChange(long)} would, so there is no need to call that method directly.
     *
     * @param changeId The ID of the compatibility change in question.
     * @return {@code true} if the change is enabled for the current app.
     */
    @CorePlatformApi(status = CorePlatformApi.Status.STABLE)
    @IntraCoreApi
    public static boolean isChangeEnabled(@ChangeId long changeId) {
        return sCallbacks.isChangeEnabled(changeId);
    }

    private static final BehaviorChangeDelegate DEFAULT_CALLBACKS = new BehaviorChangeDelegate(){};

    private volatile static BehaviorChangeDelegate sCallbacks = DEFAULT_CALLBACKS;

    /**
     * Sets the behavior change delegate.
     *
     * All changes reported via the {@link Compatibility} class will be forwarded to this class.
     */
    @CorePlatformApi(status = CorePlatformApi.Status.STABLE)
    public static void setBehaviorChangeDelegate(BehaviorChangeDelegate callbacks) {
        sCallbacks = Objects.requireNonNull(callbacks);
    }

    /**
     * Removes a behavior change delegate previously set via {@link #setBehaviorChangeDelegate}.
     */
    @CorePlatformApi(status = CorePlatformApi.Status.STABLE)
    public static void clearBehaviorChangeDelegate() {
        sCallbacks = DEFAULT_CALLBACKS;
    }

    /**
     * For use by tests only. Causes values from {@code overrides} to be returned instead of the
     * real value.
     */
    @CorePlatformApi(status = CorePlatformApi.Status.STABLE)
    public static void setOverrides(ChangeConfig overrides) {
        // Setting overrides twice in a row does not need to be supported because
        // this method is only for enabling/disabling changes for the duration of
        // a single test.
        // In production, the app is restarted when changes get enabled or disabled,
        // and the ChangeConfig is then set exactly once on that app process.
        if (sCallbacks instanceof OverrideCallbacks) {
            throw new IllegalStateException("setOverrides has already been called!");
        }
        sCallbacks = new OverrideCallbacks(sCallbacks, overrides);
    }

    /**
     * For use by tests only. Removes overrides set by {@link #setOverrides}.
     */
    @CorePlatformApi(status = CorePlatformApi.Status.STABLE)
    public static void clearOverrides() {
        if (!(sCallbacks instanceof OverrideCallbacks)) {
            throw new IllegalStateException("No overrides set");
        }
        sCallbacks = ((OverrideCallbacks) sCallbacks).delegate;
    }

    /**
     * Base class for compatibility API implementations. The default implementation logs a warning
     * to logcat.
     *
     * This is provided as a class rather than an interface to allow new methods to be added without
     * breaking @CorePlatformApi binary compatibility.
     */
    @CorePlatformApi(status = CorePlatformApi.Status.STABLE)
    public interface BehaviorChangeDelegate {
        /**
         * Called when a change is reported via {@link Compatibility#reportUnconditionalChange}
         */
        @CorePlatformApi(status = CorePlatformApi.Status.STABLE)
        default void onChangeReported(long changeId) {
            // Do not use String.format here (b/160912695)
            System.logW("No Compatibility callbacks set! Reporting change " + changeId);
        }

        /**
         * Called when a change is queried via {@link Compatibility#isChangeEnabled}
         */
        @CorePlatformApi(status = CorePlatformApi.Status.STABLE)
        default boolean isChangeEnabled(long changeId) {
            // Do not use String.format here (b/160912695)
            System.logW("No Compatibility callbacks set! Querying change " + changeId);
            return true;
        }
    }

    @CorePlatformApi(status = CorePlatformApi.Status.STABLE)
    @IntraCoreApi
    public static final class ChangeConfig {
        private final Set<Long> enabled;
        private final Set<Long> disabled;

        public ChangeConfig(Set<Long> enabled, Set<Long> disabled) {
            this.enabled = Objects.requireNonNull(enabled);
            this.disabled = Objects.requireNonNull(disabled);
            if (enabled.contains(null)) {
                throw new NullPointerException();
            }
            if (disabled.contains(null)) {
                throw new NullPointerException();
            }
            Set<Long> intersection = new HashSet<>(enabled);
            intersection.retainAll(disabled);
            if (!intersection.isEmpty()) {
                throw new IllegalArgumentException("Cannot have changes " + intersection
                        + " enabled and disabled!");
            }
        }

        public boolean isEmpty() {
            return enabled.isEmpty() && disabled.isEmpty();
        }

        private static long[] toLongArray(Set<Long> values) {
            long[] result = new long[values.size()];
            int idx = 0;
            for (Long value: values) {
                result[idx++] = value;
            }
            return result;
        }

        public long[] forceEnabledChangesArray() {
            return toLongArray(enabled);
        }

        public long[] forceDisabledChangesArray() {
            return toLongArray(disabled);
        }

        public Set<Long> forceEnabledSet() {
            return Collections.unmodifiableSet(enabled);
        }

        public Set<Long> forceDisabledSet() {
            return Collections.unmodifiableSet(disabled);
        }

        public boolean isForceEnabled(long changeId) {
            return enabled.contains(changeId);
        }

        public boolean isForceDisabled(long changeId) {
            return disabled.contains(changeId);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ChangeConfig)) {
                return false;
            }
            ChangeConfig that = (ChangeConfig) o;
            return enabled.equals(that.enabled) &&
                    disabled.equals(that.disabled);
        }

        @Override
        public int hashCode() {
            return Objects.hash(enabled, disabled);
        }

        @Override
        public String toString() {
            return "ChangeConfig{enabled=" + enabled + ", disabled=" + disabled + '}';
        }
    }

    private static class OverrideCallbacks implements BehaviorChangeDelegate {
        private final BehaviorChangeDelegate delegate;
        private final ChangeConfig changeConfig;

        private OverrideCallbacks(BehaviorChangeDelegate delegate, ChangeConfig changeConfig) {
            this.delegate = Objects.requireNonNull(delegate);
            this.changeConfig = Objects.requireNonNull(changeConfig);
        }
        @Override
        public boolean isChangeEnabled(long changeId) {
           if (changeConfig.isForceEnabled(changeId)) {
               return true;
           }
           if (changeConfig.isForceDisabled(changeId)) {
               return false;
           }
           return delegate.isChangeEnabled(changeId);
        }
    }
}
