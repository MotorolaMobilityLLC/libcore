/*
 * Copyright (C) 2015 The Android Open Source Project
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
package libcore.tzdata.update2;

import android.util.Slog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import libcore.tzdata.shared2.DistroException;
import libcore.tzdata.shared2.DistroVersion;
import libcore.tzdata.shared2.FileUtils;
import libcore.tzdata.shared2.TimeZoneDistro;
import libcore.util.ZoneInfoDB;

/**
 * A distro-validation / extraction class. Separate from the services code that uses it for easier
 * testing. This class is not thread-safe: callers are expected to handle mutual exclusion.
 */
public class TimeZoneDistroInstaller {
    /** {@link #installWithErrorCode(byte[])} result code: Success. */
    public final static int INSTALL_SUCCESS = 0;
    /** {@link #installWithErrorCode(byte[])} result code: Distro corrupt. */
    public final static int INSTALL_FAIL_BAD_DISTRO_STRUCTURE = 1;
    /** {@link #installWithErrorCode(byte[])} result code: Distro version incompatible. */
    public final static int INSTALL_FAIL_BAD_DISTRO_FORMAT_VERSION = 2;
    /** {@link #installWithErrorCode(byte[])} result code: Distro rules too old for device. */
    public final static int INSTALL_FAIL_RULES_TOO_OLD = 3;
    /** {@link #installWithErrorCode(byte[])} result code: Distro content failed validation. */
    public final static int INSTALL_FAIL_VALIDATION_ERROR = 4;

    private static final String CURRENT_TZ_DATA_DIR_NAME = "current";
    private static final String WORKING_DIR_NAME = "working";
    private static final String OLD_TZ_DATA_DIR_NAME = "old";

    private final String logTag;
    private final File systemTzDataFile;
    private final File oldTzDataDir;
    private final File currentTzDataDir;
    private final File workingDir;

    public TimeZoneDistroInstaller(String logTag, File systemTzDataFile, File installDir) {
        this.logTag = logTag;
        this.systemTzDataFile = systemTzDataFile;
        oldTzDataDir = new File(installDir, OLD_TZ_DATA_DIR_NAME);
        currentTzDataDir = new File(installDir, CURRENT_TZ_DATA_DIR_NAME);
        workingDir = new File(installDir, WORKING_DIR_NAME);
    }

    // VisibleForTesting
    File getOldTzDataDir() {
        return oldTzDataDir;
    }

    // VisibleForTesting
    File getCurrentTzDataDir() {
        return currentTzDataDir;
    }

    // VisibleForTesting
    File getWorkingDir() {
        return workingDir;
    }

    /**
     * Install the supplied content.
     *
     * <p>Errors during unpacking or installation will throw an {@link IOException}.
     * If the distro content is invalid this method returns {@code false}.
     * If the installation completed successfully this method returns {@code true}.
     */
    public boolean install(byte[] content) throws IOException {
        int result = installWithErrorCode(content);
        return result == INSTALL_SUCCESS;
    }

    /**
     * Install the supplied time zone distro.
     *
     * <p>Errors during unpacking or installation will throw an {@link IOException}.
     * Returns {@link #INSTALL_SUCCESS} or an error code.
     */
    public int installWithErrorCode(byte[] content) throws IOException {
        if (oldTzDataDir.exists()) {
            FileUtils.deleteRecursive(oldTzDataDir);
        }
        if (workingDir.exists()) {
            FileUtils.deleteRecursive(workingDir);
        }

        Slog.i(logTag, "Unpacking / verifying time zone update");
        unpackDistro(content, workingDir);
        try {
            DistroVersion distroVersion;
            try {
                distroVersion = readDistroVersion(workingDir);
            } catch (DistroException e) {
                Slog.i(logTag, "Invalid distro version: " + e.getMessage());
                return INSTALL_FAIL_BAD_DISTRO_STRUCTURE;
            }
            if (distroVersion == null) {
                Slog.i(logTag, "Update not applied: Distro version could not be loaded");
                return INSTALL_FAIL_BAD_DISTRO_STRUCTURE;
            }
            if (!DistroVersion.isCompatibleWithThisDevice(distroVersion)) {
                Slog.i(logTag, "Update not applied: Distro format version check failed: "
                        + distroVersion);
                return INSTALL_FAIL_BAD_DISTRO_FORMAT_VERSION;
            }

            if (!checkDistroDataFilesExist(workingDir)) {
                Slog.i(logTag, "Update not applied: Distro is missing required data file(s)");
                return INSTALL_FAIL_BAD_DISTRO_STRUCTURE;
            }

            if (!checkDistroRulesNewerThanSystem(systemTzDataFile, distroVersion)) {
                Slog.i(logTag, "Update not applied: Distro rules version check failed");
                return INSTALL_FAIL_RULES_TOO_OLD;
            }

            File zoneInfoFile = new File(workingDir, TimeZoneDistro.TZDATA_FILE_NAME);
            ZoneInfoDB.TzData tzData = ZoneInfoDB.TzData.loadTzData(zoneInfoFile.getPath());
            if (tzData == null) {
                Slog.i(logTag, "Update not applied: " + zoneInfoFile + " could not be loaded");
                return INSTALL_FAIL_VALIDATION_ERROR;
            }
            try {
                tzData.validate();
            } catch (IOException e) {
                Slog.i(logTag, "Update not applied: " + zoneInfoFile + " failed validation", e);
                return INSTALL_FAIL_VALIDATION_ERROR;
            } finally {
                tzData.close();
            }
            // TODO(nfuller): Add deeper validity checks / canarying before applying.
            // http://b/31008728

            Slog.i(logTag, "Applying time zone update");
            FileUtils.makeDirectoryWorldAccessible(workingDir);

            if (currentTzDataDir.exists()) {
                Slog.i(logTag, "Moving " + currentTzDataDir + " to " + oldTzDataDir);
                FileUtils.rename(currentTzDataDir, oldTzDataDir);
            }
            Slog.i(logTag, "Moving " + workingDir + " to " + currentTzDataDir);
            FileUtils.rename(workingDir, currentTzDataDir);
            Slog.i(logTag, "Update applied: " + currentTzDataDir + " successfully created");
            return INSTALL_SUCCESS;
        } finally {
            deleteBestEffort(oldTzDataDir);
            deleteBestEffort(workingDir);
        }
    }

    /**
     * Uninstall the current timezone update in /data, returning the device to using data from
     * /system. Returns {@code true} if uninstallation was successful, {@code false} if there was
     * nothing installed in /data to uninstall.
     *
     * <p>Errors encountered during uninstallation will throw an {@link IOException}.
     */
    public boolean uninstall() throws IOException {
        Slog.i(logTag, "Uninstalling time zone update");

        // Make sure we don't have a dir where we're going to move the currently installed data to.
        if (oldTzDataDir.exists()) {
            // If we can't remove this, an exception is thrown and we don't continue.
            FileUtils.deleteRecursive(oldTzDataDir);
        }

        if (!currentTzDataDir.exists()) {
            Slog.i(logTag, "Nothing to uninstall at " + currentTzDataDir);
            return false;
        }

        Slog.i(logTag, "Moving " + currentTzDataDir + " to " + oldTzDataDir);
        // Move currentTzDataDir out of the way in one operation so we can't partially delete
        // the contents, which would leave a partial install.
        FileUtils.rename(currentTzDataDir, oldTzDataDir);

        // Do our best to delete the now uninstalled timezone data.
        deleteBestEffort(oldTzDataDir);

        Slog.i(logTag, "Time zone update uninstalled.");

        return true;
    }

    /**
     * Reads the currently installed distro version. Returns {@code null} if there is no distro
     * installed.
     *
     * @throws IOException if there was a problem reading data from /data
     * @throws DistroException if there was a problem with the installed distro format/structure
     */
    public DistroVersion getInstalledDistroVersion() throws DistroException, IOException {
        if (!currentTzDataDir.exists()) {
            return null;
        }
        return readDistroVersion(currentTzDataDir);
    }

    /**
     * Reads the timezone rules version present in /system. i.e. the version that would be present
     * after a factory reset.
     *
     * @throws IOException if there was a problem reading data
     */
    public String getSystemRulesVersion() throws IOException {
        return readSystemRulesVersion(systemTzDataFile);
    }

    private void deleteBestEffort(File dir) {
        if (dir.exists()) {
            Slog.i(logTag, "Deleting " + dir);
            try {
                FileUtils.deleteRecursive(dir);
            } catch (IOException e) {
                // Logged but otherwise ignored.
                Slog.w(logTag, "Unable to delete " + dir, e);
            }
        }
    }

    private void unpackDistro(byte[] content, File targetDir) throws IOException {
        Slog.i(logTag, "Unpacking update content to: " + targetDir);
        TimeZoneDistro distro = new TimeZoneDistro(content);
        distro.extractTo(targetDir);
    }

    private boolean checkDistroDataFilesExist(File unpackedContentDir) throws IOException {
        Slog.i(logTag, "Verifying distro contents");
        return FileUtils.filesExist(unpackedContentDir,
                TimeZoneDistro.TZDATA_FILE_NAME,
                TimeZoneDistro.ICU_DATA_FILE_NAME);
    }

    private DistroVersion readDistroVersion(File distroDir) throws DistroException, IOException {
        Slog.i(logTag, "Reading distro format version");
        File distroVersionFile = new File(distroDir, TimeZoneDistro.DISTRO_VERSION_FILE_NAME);
        if (!distroVersionFile.exists()) {
            throw new DistroException("No distro version file found: " + distroVersionFile);
        }
        byte[] versionBytes =
                FileUtils.readBytes(distroVersionFile, DistroVersion.DISTRO_VERSION_FILE_LENGTH);
        return DistroVersion.fromBytes(versionBytes);
    }

    /**
     * Returns true if the the distro IANA rules version is >= system IANA rules version.
     */
    private boolean checkDistroRulesNewerThanSystem(
            File systemTzDataFile, DistroVersion distroVersion) throws IOException {

        // We only check the /system tzdata file and assume that other data like ICU is in sync.
        // There is a CTS test that checks ICU and bionic/libcore are in sync.
        Slog.i(logTag, "Reading /system rules version");
        String systemRulesVersion = readSystemRulesVersion(systemTzDataFile);

        String distroRulesVersion = distroVersion.rulesVersion;
        // canApply = distroRulesVersion >= systemRulesVersion
        boolean canApply = distroRulesVersion.compareTo(systemRulesVersion) >= 0;
        if (!canApply) {
            Slog.i(logTag, "Failed rules version check: distroRulesVersion="
                    + distroRulesVersion + ", systemRulesVersion=" + systemRulesVersion);
        } else {
            Slog.i(logTag, "Passed rules version check: distroRulesVersion="
                    + distroRulesVersion + ", systemRulesVersion=" + systemRulesVersion);
        }
        return canApply;
    }

    private String readSystemRulesVersion(File systemTzDataFile) throws IOException {
        if (!systemTzDataFile.exists()) {
            Slog.i(logTag, "tzdata file cannot be found in /system");
            throw new FileNotFoundException("system tzdata does not exist: " + systemTzDataFile);
        }
        return ZoneInfoDB.TzData.getRulesVersion(systemTzDataFile);
    }
}
