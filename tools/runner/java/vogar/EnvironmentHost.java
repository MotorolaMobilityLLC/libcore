/*
 * Copyright (C) 2010 The Android Open Source Project
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

package vogar;

import vogar.commands.Command;
import vogar.commands.Mkdir;

import java.io.File;

class EnvironmentHost extends Environment {

    EnvironmentHost(boolean cleanBefore, boolean cleanAfter,
            Integer debugPort, File localTemp) {
        super(cleanBefore, cleanAfter, debugPort, localTemp);
    }

    @Override void prepare() {}

    @Override protected void prepareUserDir(Action action) {
        File actionUserDir = actionUserDir(action);

        // if the user dir exists, cp would copy the files to the wrong place
        if (actionUserDir.exists()) {
            throw new IllegalStateException();
        }

        new Mkdir().mkdirs(actionUserDir.getParentFile());
        new Command("cp", "-r", action.getJavaDirectory().toString(),
                actionUserDir.toString()).execute();
        action.setUserDir(actionUserDir);
    }

    @Override void shutdown() {}
}
