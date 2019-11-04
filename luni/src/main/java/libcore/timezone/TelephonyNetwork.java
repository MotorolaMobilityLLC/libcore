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
 * limitations under the License.
 */

package libcore.timezone;

import static libcore.timezone.XmlUtils.normalizeCountryIso;

import java.util.Objects;

/**
 * Information about a telephony network.
 *
 * @hide
 */
@libcore.api.CorePlatformApi
public class TelephonyNetwork {

    /**
     * A numeric network identifier consisting of the Mobile Country Code (MCC) and the Mobile
     * Network Code (MNC).
     *
     * @hide
     */
    public static final class MccMnc {
        final int mcc;
        final int mnc;

        public MccMnc(int mcc, int mnc) {
            this.mcc = mcc;
            this.mnc = mnc;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MccMnc mccMnc = (MccMnc) o;
            return mcc == mccMnc.mcc
                    && mnc == mccMnc.mnc;
        }

        @Override
        public int hashCode() {
            return Objects.hash(mcc, mnc);
        }

        @Override
        public String toString() {
            return "MccMnc{"
                    + "mcc=" + mcc
                    + ", mnc=" + mnc
                    + '}';
        }
    }

    private final MccMnc mccMnc;
    private final String countryIsoCode;

    public static TelephonyNetwork create(int mcc, int mnc, String countryIsoCode) {
        String normalizedCountryIso = normalizeCountryIso(countryIsoCode);
        return new TelephonyNetwork(new MccMnc(mcc, mnc), normalizedCountryIso);
    }

    private TelephonyNetwork(MccMnc mccMnc, String countryIsoCode) {
        this.mccMnc = mccMnc;
        this.countryIsoCode = Objects.requireNonNull(countryIsoCode);
    }

    public MccMnc getMccMnc() {
        return mccMnc;
    }

    @libcore.api.CorePlatformApi
    public int getMcc() {
        return mccMnc.mcc;
    }

    @libcore.api.CorePlatformApi
    public int getMnc() {
        return mccMnc.mnc;
    }

    @libcore.api.CorePlatformApi
    public String getCountryIsoCode() {
        return countryIsoCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TelephonyNetwork that = (TelephonyNetwork) o;
        return mccMnc.equals(that.mccMnc) &&
                countryIsoCode.equals(that.countryIsoCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mccMnc, countryIsoCode);
    }

    @Override
    public String toString() {
        return "TelephonyNetwork{"
                + "mccMnc=" + mccMnc
                + ", countryIsoCode='" + countryIsoCode + '\''
                + '}';
    }
}
