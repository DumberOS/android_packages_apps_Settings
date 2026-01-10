/*
 * Copyright (C) 2025 The Android Open Source Project
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

package com.android.settings.inputmethod;

import android.content.Context;
import android.os.UserHandle;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.TogglePreferenceController;

public class KeyboardBacklightTogglePreferenceController extends TogglePreferenceController
        implements PreferenceControllerMixin {

    public KeyboardBacklightTogglePreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public boolean isChecked() {
        return Settings.System.getIntForUser(mContext.getContentResolver(),
                Settings.System.KEYBOARD_BACKLIGHT_ENABLED, 1, UserHandle.USER_CURRENT) != 0;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        Settings.System.putIntForUser(mContext.getContentResolver(),
                Settings.System.KEYBOARD_BACKLIGHT_ENABLED, isChecked ? 1 : 0,
                UserHandle.USER_CURRENT);
        return true;
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_system;
    }
}
