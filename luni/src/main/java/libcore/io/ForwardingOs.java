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

import java.io.FileDescriptor;

/**
 * Subclass this if you want to override some {@link Os} methods but otherwise delegate.
 */
public class ForwardingOs implements Os {
    protected final Os os;

    public ForwardingOs(Os os) {
        this.os = os;
    }

    public boolean access(String path, int mode) throws ErrnoException { return os.access(path, mode); }
    public String[] environ() { return os.environ(); }
    public void fdatasync(FileDescriptor fd) throws ErrnoException { os.fdatasync(fd); }
    public void fsync(FileDescriptor fd) throws ErrnoException { os.fsync(fd); }
    public String getenv(String name) { return os.getenv(name); }
    public String strerror(int errno) { return os.strerror(errno); }
}
