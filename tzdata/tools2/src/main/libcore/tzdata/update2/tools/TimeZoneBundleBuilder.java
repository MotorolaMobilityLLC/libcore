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
package libcore.tzdata.update2.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import libcore.tzdata.update2.BundleException;
import libcore.tzdata.update2.BundleVersion;
import libcore.tzdata.update2.TimeZoneBundle;

/**
 * A class for creating a {@link TimeZoneBundle} containing timezone update data. Used in real
 * bundle creation code and tests.
 */
public final class TimeZoneBundleBuilder {

    private BundleVersion bundleVersion;
    private byte[] tzData;
    private byte[] icuData;

    public TimeZoneBundleBuilder setBundleVersion(BundleVersion bundleVersion) {
        this.bundleVersion = bundleVersion;
        return this;
    }

    public TimeZoneBundleBuilder clearVersionForTests() {
        // This has the effect of omitting the version file in buildUnvalidated().
        this.bundleVersion = null;
        return this;
    }

    public TimeZoneBundleBuilder replaceFormatVersionForTests(int majorVersion, int minorVersion) {
        try {
            bundleVersion = new BundleVersion(
                    majorVersion, minorVersion, bundleVersion.rulesVersion, bundleVersion.revision);
        } catch (BundleException e) {
            throw new IllegalArgumentException();
        }
        return this;
    }

    public TimeZoneBundleBuilder setTzData(File tzDataFile) throws IOException {
        return setTzData(readFileAsByteArray(tzDataFile));
    }

    public TimeZoneBundleBuilder setTzData(byte[] tzData) {
        this.tzData = tzData;
        return this;
    }

    // For use in tests.
    public TimeZoneBundleBuilder clearTzDataForTests() {
        this.tzData = null;
        return this;
    }

    public TimeZoneBundleBuilder setIcuData(File icuDataFile) throws IOException {
        return setIcuData(readFileAsByteArray(icuDataFile));
    }

    public TimeZoneBundleBuilder setIcuData(byte[] icuData) {
        this.icuData = icuData;
        return this;
    }

    // For use in tests.
    public TimeZoneBundleBuilder clearIcuDataForTests() {
        this.icuData = null;
        return this;
    }

    /**
     * For use in tests. Use {@link #build()}.
     */
    public TimeZoneBundle buildUnvalidated() throws BundleException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            if (bundleVersion != null) {
                addZipEntry(zos, TimeZoneBundle.BUNDLE_VERSION_FILE_NAME, bundleVersion.toBytes());
            }

            if (tzData != null) {
                addZipEntry(zos, TimeZoneBundle.TZDATA_FILE_NAME, tzData);
            }
            if (icuData != null) {
                addZipEntry(zos, TimeZoneBundle.ICU_DATA_FILE_NAME, icuData);
            }
        } catch (IOException e) {
            throw new BundleException("Unable to create zip file", e);
        }
        return new TimeZoneBundle(baos.toByteArray());
    }

    /**
     * Builds a {@link TimeZoneBundle}.
     */
    public TimeZoneBundle build() throws BundleException {
        if (bundleVersion == null) {
            throw new IllegalStateException("Missing bundleVersion");
        }
        if (icuData == null) {
            throw new IllegalStateException("Missing icuData");
        }
        if (tzData == null) {
            throw new IllegalStateException("Missing tzData");
        }
        return buildUnvalidated();
    }

    private static void addZipEntry(ZipOutputStream zos, String name, byte[] content)
            throws BundleException {
        try {
            ZipEntry zipEntry = new ZipEntry(name);
            zipEntry.setSize(content.length);
            zos.putNextEntry(zipEntry);
            zos.write(content);
            zos.closeEntry();
        } catch (IOException e) {
            throw new BundleException("Unable to add zip entry", e);
        }
    }

    /**
     * Returns the contents of 'path' as a byte array.
     */
    public static byte[] readFileAsByteArray(File file) throws IOException {
        byte[] buffer = new byte[8192];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (FileInputStream  fis = new FileInputStream(file)) {
            int count;
            while ((count = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, count);
            }
        }
        return baos.toByteArray();
    }
}

