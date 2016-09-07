/*
 * Copyright (C) 2006 The Android Open Source Project
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

package dalvik.system;

import java.io.File;

/**
 * Provides hooks for the zygote to call back into the runtime to perform
 * parent or child specific initialization..
 *
 * @hide
 */
public final class ZygoteHooks {
    private long token;

    /**
     * Called by the zygote prior to every fork. Each call to {@code preFork}
     * is followed by a matching call to {@link #postForkChild(int)} on the child
     * process and {@link #postForkCommon()} on both the parent and the child
     * process. {@code postForkCommon} is called after {@code postForkChild} in
     * the child process.
     */
    public void preFork() {
        Daemons.stop();
        waitUntilAllThreadsStopped();
        token = nativePreFork();
        System.closeLogSockets();
    }

    /**
     * Called by the zygote in the child process after every fork. The debug
     * flags from {@code debugFlags} are applied to the child process. The string
     * {@code instructionSet} determines whether to use a native bridge.
     */
    public void postForkChild(int debugFlags, String instructionSet) {
        nativePostForkChild(token, debugFlags, instructionSet);
    }

    /**
     * Called by the zygote in both the parent and child processes after
     * every fork. In the child process, this method is called after
     * {@code postForkChild}.
     */
    public void postForkCommon() {
        Daemons.start();
    }

    private static native long nativePreFork();
    private static native void nativePostForkChild(long token, int debugFlags,
                                                   String instructionSet);

    /**
     * We must not fork until we're single-threaded again. Wait until /proc shows we're
     * down to just one thread.
     */
    private static void waitUntilAllThreadsStopped() {
        File tasks = new File("/proc/self/task");
        while (tasks.list().length > 1) {
            try {
                // Experimentally, booting and playing about with a stingray, I never saw us
                // go round this loop more than once with a 10ms sleep.
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
