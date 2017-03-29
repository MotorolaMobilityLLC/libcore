/*
 * Copyright (C) 2017 The Android Open Source Project
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

package libcore.tzdata.shared2;

/**
 * Information about a staged time zone distro operation.
 */
public class StagedDistroOperation {

    private static final StagedDistroOperation UNINSTALL_STAGED =
            new StagedDistroOperation(true /* isUninstall */, null /* stagedVersion */);

    public final boolean isUninstall;
    public final DistroVersion distroVersion;

    private StagedDistroOperation(boolean isUninstall, DistroVersion distroVersion) {
        this.isUninstall = isUninstall;
        this.distroVersion = distroVersion;
    }

    public static StagedDistroOperation install(DistroVersion distroVersion) {
        if (distroVersion == null) {
            throw new NullPointerException("distroVersion==null");
        }
        return new StagedDistroOperation(false /* isUninstall */, distroVersion);
    }

    public static StagedDistroOperation uninstall() {
        return UNINSTALL_STAGED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StagedDistroOperation that = (StagedDistroOperation) o;

        if (isUninstall != that.isUninstall) {
            return false;
        }
        return distroVersion != null ? distroVersion.equals(that.distroVersion)
                : that.distroVersion == null;
    }

    @Override
    public int hashCode() {
        int result = (isUninstall ? 1 : 0);
        result = 31 * result + (distroVersion != null ? distroVersion.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StagedDistroOperation{" +
                "isUninstall=" + isUninstall +
                ", distroVersion=" + distroVersion +
                '}';
    }
}
