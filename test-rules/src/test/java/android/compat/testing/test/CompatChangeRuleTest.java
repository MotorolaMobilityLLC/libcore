/*
 * Copyright (C) 2018 The Android Open Source Project
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
package android.compat.testing.test;

import static com.google.common.truth.Truth.assertThat;
import static android.compat.testing.CompatChangeRule.TestType.HOST_SIDE;

import android.compat.testing.CompatChangeRule;
import android.compat.testing.CompatChangeRule.DisableCompatChanges;
import android.compat.testing.CompatChangeRule.EnableCompatChanges;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for platform compatibility change gating.
 */
@RunWith(JUnit4.class)
public class CompatChangeRuleTest {

    @Rule
    public TestRule compatChangeRule = new CompatChangeRule(HOST_SIDE);

    @Test
    @EnableCompatChanges({DummyApi.CHANGE_ID})
    public void testDummyGatingPositive() {
        assertThat(DummyApi.dummyFunc()).isEqualTo("A");
    }

    @Test
    @DisableCompatChanges({DummyApi.CHANGE_ID})
    public void testDummyGatingNegative() {
        assertThat(DummyApi.dummyFunc()).isEqualTo("B");
    }

    @Test
    @DisableCompatChanges({DummyApi.CHANGE_ID_1, DummyApi.CHANGE_ID_2})
    public void testDummyGatingCombined0() {
        assertThat(DummyApi.dummyCombinedFunc()).isEqualTo("0");
    }

    @Test
    @DisableCompatChanges({DummyApi.CHANGE_ID_1})
    @EnableCompatChanges({DummyApi.CHANGE_ID_2})
    public void testDummyGatingCombined1() {
        assertThat(DummyApi.dummyCombinedFunc()).isEqualTo("1");
    }

    @Test
    @EnableCompatChanges({DummyApi.CHANGE_ID_1})
    @DisableCompatChanges({DummyApi.CHANGE_ID_2})
    public void testDummyGatingCombined2() {
        assertThat(DummyApi.dummyCombinedFunc()).isEqualTo("2");
    }

    @Test
    @EnableCompatChanges({DummyApi.CHANGE_ID_1, DummyApi.CHANGE_ID_2})
    public void testDummyGatingCombined3() {
        assertThat(DummyApi.dummyCombinedFunc()).isEqualTo("3");
    }
}