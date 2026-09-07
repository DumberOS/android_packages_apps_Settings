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

package com.android.settings.network.telephony

import android.app.AlertDialog
import android.content.Context
import android.os.PowerManager
import android.os.SystemProperties
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import com.android.settings.R
import com.android.settingslib.spa.framework.util.collectLatestWithLifecycle
import java.io.File
import java.io.IOException

/** Allows users of the stock MTK IMS stack to return to TrebleApp IMS. */
class SwitchToTrebleAppImsPreferenceController(context: Context, key: String) :
    TelephonyBasePreferenceController(context, key) {

    private companion object {
        const val TAG = "SwitchToTrebleAppIms"
        const val STOCK_MTK_IMS_PROPERTY = "sys.phh.stock_mtk_ims"
        const val STOCK_MTK_IMS_FLAG = "/metadata/phh/dumber_mini_stock_ims"
    }

    private lateinit var preference: Preference
    private lateinit var callingPreferenceCategoryController: CallingPreferenceCategoryController

    fun init(
        subId: Int,
        callingPreferenceCategoryController: CallingPreferenceCategoryController,
    ): SwitchToTrebleAppImsPreferenceController {
        mSubId = subId
        this.callingPreferenceCategoryController = callingPreferenceCategoryController
        return this
    }

    override fun getAvailabilityStatus(subId: Int): Int = when {
        SubscriptionManager.isValidSubscriptionId(subId) && isStockMtkImsActive() -> AVAILABLE
        else -> CONDITIONALLY_UNAVAILABLE
    }

    override fun displayPreference(screen: PreferenceScreen) {
        super.displayPreference(screen)
        preference = screen.findPreference(preferenceKey)!!
        val visible = isStockMtkImsActive()
        preference.isVisible = visible
        callingPreferenceCategoryController.updateChildVisible(preferenceKey, visible)
    }

    override fun onViewCreated(viewLifecycleOwner: LifecycleOwner) {
        mContext.callStateFlow(mSubId).collectLatestWithLifecycle(viewLifecycleOwner) {
            preference.isEnabled = (it == TelephonyManager.CALL_STATE_IDLE)
        }
    }

    override fun handlePreferenceTreeClick(preference: Preference): Boolean {
        if (preference.key != preferenceKey) return false

        AlertDialog.Builder(mContext)
            .setTitle(R.string.wifi_calling_treble_app_ims_dialog_title)
            .setMessage(R.string.wifi_calling_treble_app_ims_dialog_message)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.wifi_calling_stock_ims_restart_button) { _, _ ->
                selectTrebleAppImsAndReboot()
            }
            .show()
        return true
    }

    private fun selectTrebleAppImsAndReboot() {
        val flag = File(STOCK_MTK_IMS_FLAG)
        try {
            if (flag.exists() && !flag.delete()) {
                throw IOException("Failed to remove ${flag.absolutePath}")
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to select the TrebleApp IMS stack", e)
            showToast(R.string.wifi_calling_treble_app_ims_disable_failed)
            return
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission denied selecting the TrebleApp IMS stack", e)
            showToast(R.string.wifi_calling_treble_app_ims_disable_failed)
            return
        }

        try {
            val powerManager = mContext.getSystemService(PowerManager::class.java)
                ?: throw IllegalStateException("PowerManager unavailable")
            powerManager.reboot("treble_app_ims")
        } catch (e: RuntimeException) {
            Log.e(TAG, "Failed to reboot after selecting the TrebleApp IMS stack", e)
            showToast(R.string.wifi_calling_treble_app_ims_restart_manually)
        }
    }

    private fun isStockMtkImsActive(): Boolean {
        return SystemProperties.getBoolean(STOCK_MTK_IMS_PROPERTY, false)
    }

    private fun showToast(message: Int) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show()
    }
}
