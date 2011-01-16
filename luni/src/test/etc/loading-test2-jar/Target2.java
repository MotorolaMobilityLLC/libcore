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

package test2;

/**
 * Class which is probed at by the class loading tests.
 */
public class Target2 {
    public static int staticIgram = 0;
    public int instanceMagri = 0;

    public Target2() {
        // This space intentionally left blank.
    }

    public static String frotz() {
        return "frotz";
    }

    public static void setStaticIgram(int n) {
        staticIgram = n;
    }

    public String fizmo() {
        return "fizmo";
    }

    public void setInstanceMagri(int n) {
        instanceMagri = n;
    }
}
