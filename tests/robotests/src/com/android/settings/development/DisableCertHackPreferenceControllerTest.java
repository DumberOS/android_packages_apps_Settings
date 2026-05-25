/*
 * Copyright (C) 2026 The Android Open Source Project
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

package com.android.settings.development;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.os.SystemProperties;

import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class DisableCertHackPreferenceControllerTest {

    @Mock
    private SwitchPreference mPreference;
    @Mock
    private PreferenceScreen mPreferenceScreen;

    private Context mContext;
    private DisableCertHackPreferenceController mController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mContext = RuntimeEnvironment.application;
        SystemProperties.set(DisableCertHackPreferenceController.DISABLE_CERTHACK_PROPERTY,
                Boolean.toString(false));
        mController = new DisableCertHackPreferenceController(mContext);
        when(mPreferenceScreen.findPreference(mController.getPreferenceKey())).thenReturn(
                mPreference);
        mController.displayPreference(mPreferenceScreen);
    }

    @Test
    public void onPreferenceChanged_settingEnabled_turnOnDisableCertHack() {
        mController.onPreferenceChange(mPreference, true);

        final boolean disabled = SystemProperties.getBoolean(
                DisableCertHackPreferenceController.DISABLE_CERTHACK_PROPERTY, false);

        assertThat(disabled).isTrue();
    }

    @Test
    public void onPreferenceChanged_settingDisabled_turnOffDisableCertHack() {
        mController.onPreferenceChange(mPreference, false);

        final boolean disabled = SystemProperties.getBoolean(
                DisableCertHackPreferenceController.DISABLE_CERTHACK_PROPERTY, false);

        assertThat(disabled).isFalse();
    }

    @Test
    public void updateState_settingEnabled_preferenceShouldBeChecked() {
        SystemProperties.set(DisableCertHackPreferenceController.DISABLE_CERTHACK_PROPERTY,
                Boolean.toString(true));
        mController.updateState(mPreference);

        verify(mPreference).setChecked(true);
    }

    @Test
    public void updateState_settingDisabled_preferenceShouldNotBeChecked() {
        SystemProperties.set(DisableCertHackPreferenceController.DISABLE_CERTHACK_PROPERTY,
                Boolean.toString(false));
        mController.updateState(mPreference);

        verify(mPreference).setChecked(false);
    }

    @Test
    public void onDeveloperOptionsDisabled_shouldDisablePreference() {
        SystemProperties.set(DisableCertHackPreferenceController.DISABLE_CERTHACK_PROPERTY,
                Boolean.toString(true));

        mController.onDeveloperOptionsDisabled();

        verify(mPreference).setEnabled(false);
        verify(mPreference).setChecked(false);
        assertThat(SystemProperties.getBoolean(
                DisableCertHackPreferenceController.DISABLE_CERTHACK_PROPERTY, true)).isFalse();
    }
}
