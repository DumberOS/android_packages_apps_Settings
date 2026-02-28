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

package com.android.settings.notification;

import android.content.Context;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.TwoStatePreference;

import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

import java.io.File;
import java.io.IOException;

public class DisableFileSwitchPreferenceController extends AbstractPreferenceController
        implements PreferenceControllerMixin {

    private static final String TAG = "DisableFileSwitchCtl";

    private final String mPreferenceKey;
    private final File mDisableFlagFile;

    public DisableFileSwitchPreferenceController(Context context, String preferenceKey,
            String disableFlagPath) {
        super(context);
        mPreferenceKey = preferenceKey;
        mDisableFlagFile = new File(disableFlagPath);
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        final TwoStatePreference preference = screen.findPreference(mPreferenceKey);
        if (preference != null) {
            preference.setChecked(isEnabled());
        }
    }

    @Override
    public void updateState(Preference preference) {
        if (preference instanceof TwoStatePreference
                && mPreferenceKey.equals(preference.getKey())) {
            ((TwoStatePreference) preference).setChecked(isEnabled());
        }
    }

    @Override
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!mPreferenceKey.equals(preference.getKey())
                || !(preference instanceof TwoStatePreference)) {
            return false;
        }

        final boolean enabled = ((TwoStatePreference) preference).isChecked();
        if (!setEnabled(enabled)) {
            updateState(preference);
        }
        return false;
    }

    @Override
    public String getPreferenceKey() {
        return mPreferenceKey;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    private boolean isEnabled() {
        return !mDisableFlagFile.exists();
    }

    private boolean setEnabled(boolean enabled) {
        try {
            if (enabled) {
                return !mDisableFlagFile.exists() || mDisableFlagFile.delete();
            }

            final File parent = mDisableFlagFile.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                return false;
            }
            return mDisableFlagFile.exists() || mDisableFlagFile.createNewFile();
        } catch (IOException | SecurityException e) {
            Log.e(TAG, "Failed to update disable flag " + mDisableFlagFile.getAbsolutePath(), e);
            return false;
        }
    }
}
