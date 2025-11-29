/*
 * Copyright (C) 2024 The LineageOS Project
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

package com.android.settings.security.screenlock

import android.content.Context
import androidx.preference.Preference
import androidx.preference.TwoStatePreference
import com.android.internal.widget.LockPatternUtils
import com.android.internal.widget.LockPatternUtils.CREDENTIAL_TYPE_PIN
import com.android.settings.core.PreferenceControllerMixin
import com.android.settingslib.core.AbstractPreferenceController
import lineageos.providers.LineageSettings

class NumericKeyWakePreferenceController(
    context: Context,
    private val userId: Int,
    private val lockPatternUtils: LockPatternUtils
) : AbstractPreferenceController(context), PreferenceControllerMixin,
    Preference.OnPreferenceChangeListener {

    companion object {
        private const val PREF_KEY = "wake_on_numeric_key"
        private const val SETTING_DEFAULT = 1
    }

    override fun isAvailable(): Boolean {
        return lockPatternUtils.getCredentialTypeForUser(userId) == CREDENTIAL_TYPE_PIN
    }

    override fun getPreferenceKey(): String {
        return PREF_KEY
    }

    override fun onPreferenceChange(preference: Preference, value: Any): Boolean {
        LineageSettings.System.putIntForUser(
            mContext.contentResolver,
            LineageSettings.System.LOCKSCREEN_WAKE_ON_NUMERIC_KEY,
            if (value as Boolean) 1 else 0,
            userId
        )
        return true
    }

    override fun updateState(preference: Preference) {
        (preference as TwoStatePreference).isChecked = isWakeOnNumericKeyEnabled()
    }

    private fun isWakeOnNumericKeyEnabled(): Boolean {
        return LineageSettings.System.getIntForUser(
            mContext.contentResolver,
            LineageSettings.System.LOCKSCREEN_WAKE_ON_NUMERIC_KEY,
            SETTING_DEFAULT,
            userId
        ) == 1
    }
}
