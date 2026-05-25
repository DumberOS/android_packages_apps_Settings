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

import android.content.Context;
import android.os.SystemProperties;

import androidx.preference.Preference;
import androidx.preference.TwoStatePreference;

import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import com.android.settingslib.development.SystemPropPoker;

public class DisableCertHackPreferenceController extends DeveloperOptionsPreferenceController
        implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {

    static final String DISABLE_CERTHACK_PROPERTY = "persist.sys.dumbdroid.disable_certhack";
    private static final String PREFERENCE_KEY = "disable_certhack";

    public DisableCertHackPreferenceController(Context context) {
        super(context);
    }

    @Override
    public String getPreferenceKey() {
        return PREFERENCE_KEY;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final boolean disableCertHack = (Boolean) newValue;
        SystemProperties.set(DISABLE_CERTHACK_PROPERTY, Boolean.toString(disableCertHack));
        SystemPropPoker.getInstance().poke();
        return true;
    }

    @Override
    public void updateState(Preference preference) {
        final boolean disabled = SystemProperties.getBoolean(DISABLE_CERTHACK_PROPERTY, false);
        ((TwoStatePreference) mPreference).setChecked(disabled);
    }

    @Override
    protected void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        SystemProperties.set(DISABLE_CERTHACK_PROPERTY, Boolean.toString(false));
        ((TwoStatePreference) mPreference).setChecked(false);
    }
}
