/*
 * Copyright (C) 2009 The Android Open Source Project
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

package dalvik.jtreg;

import java.io.File;

/**
 * An adb command.
 */
final class Adb {

    public void mkdir(File name) {
        new Command("adb", "shell", "mkdir", name.toString()).execute();
    }

    public void rm(File name) {
        new Command("adb", "shell", "rm", "-r", name.toString()).execute();
    }

    public void push(File local, File remote) {
        new Command("adb", "push", local.toString(), remote.toString())
                .execute();
    }

    public void forwardTcp(int localPort, int devicePort) {
        new Command("adb", "forward", "tcp:" + localPort, "tcp:" + devicePort)
                .execute();
    }
}