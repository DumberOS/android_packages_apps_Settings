/*
 * Copyright (C) 2022 The Android Open Source Project
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

package com.android.settings.spa.app.appinfo

import android.content.pm.ApplicationInfo
import android.content.pm.FeatureFlags as PmFeatureFlags
import android.content.pm.FeatureFlagsImpl as PmFeatureFlagsImpl
import androidx.compose.foundation.focusGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.settingslib.applications.AppUtils
import com.android.settingslib.spa.widget.button.ActionButton
import com.android.settingslib.spa.widget.button.ActionButtons
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
/**
 * @param featureFlags can be overridden in tests
 */
fun AppButtons(
    packageInfoPresenter: PackageInfoPresenter,
    isHibernationSwitchEnabledStateFlow: MutableStateFlow<Boolean>,
    featureFlags: PmFeatureFlags = PmFeatureFlagsImpl(),
    focusRequester: FocusRequester? = null,
    upFocusRequester: FocusRequester = FocusRequester.Default,
    downFocusRequester: FocusRequester = FocusRequester.Default,
    onActionsAvailable: (Boolean) -> Unit = {},
) {
    val isMainlineModule = remember(packageInfoPresenter) {
        packageInfoPresenter.isMainlineModule()
    }
    val buttons = if (isMainlineModule) emptyList() else {
        val presenter = remember {
            AppButtonsPresenter(
                packageInfoPresenter,
                isHibernationSwitchEnabledStateFlow,
                featureFlags
            )
        }
        presenter.getActionButtons()
    }
    SideEffect { onActionsAvailable(buttons.any { it.enabled }) }
    if (focusRequester == null) {
        if (!isMainlineModule) ActionButtons(actionButtons = buttons)
    } else {
        AppInfoActionButtons(buttons, focusRequester, upFocusRequester, downFocusRequester)
    }
}

private fun PackageInfoPresenter.isMainlineModule(): Boolean =
    AppUtils.isMainlineModule(userPackageManager, packageName)

private class AppButtonsPresenter(
    private val packageInfoPresenter: PackageInfoPresenter,
    isHibernationSwitchEnabledStateFlow: MutableStateFlow<Boolean>,
    private val featureFlags: PmFeatureFlags
) {
    private val appLaunchButton = AppLaunchButton(packageInfoPresenter)
    private val appInstallButton = AppInstallButton(packageInfoPresenter)
    private val appDisableButton = AppDisableButton(packageInfoPresenter)
    private val appUninstallButton = AppUninstallButton(packageInfoPresenter)
    private val appClearButton = AppClearButton(packageInfoPresenter)
    private val appForceStopButton = AppForceStopButton(packageInfoPresenter)
    private val appArchiveButton =
        AppArchiveButton(packageInfoPresenter, isHibernationSwitchEnabledStateFlow)
    private val appRestoreButton = AppRestoreButton(packageInfoPresenter)

    @Composable
    fun getActionButtons() =
        packageInfoPresenter.flow.collectAsStateWithLifecycle().value?.let {
            getActionButtons(checkNotNull(it.applicationInfo))
        } ?: emptyList()

    @Composable
    private fun getActionButtons(app: ApplicationInfo): List<ActionButton> = listOfNotNull(
        if (isArchivingEnabled(featureFlags)) {
            if (app.isArchived) {
                appRestoreButton.getActionButton(app)
            } else {
                appArchiveButton.getActionButton(app)
            }
        } else {
            appLaunchButton.getActionButton(app)
        },
        appInstallButton.getActionButton(app),
        appDisableButton.getActionButton(app),
        appUninstallButton.getActionButton(app),
        appClearButton.getActionButton(app),
        appForceStopButton.getActionButton(app),
    )
}

/** Explicit routes keep horizontal movement in the action row, skipping disabled actions. */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun AppInfoActionButtons(
    buttons: List<ActionButton>,
    focusRequester: FocusRequester,
    upFocusRequester: FocusRequester,
    downFocusRequester: FocusRequester,
) {
    val requesters = remember(buttons.size) { List(buttons.size) { FocusRequester() } }
    val inputModeManager = LocalInputModeManager.current
    var initialFocusRequested by remember { mutableStateOf(false) }
    val firstEnabled = buttons.indexOfFirst { it.enabled }
    if (buttons.isNotEmpty()) ActionButtons(
        actionButtons = buttons,
        modifier = Modifier.focusRequester(focusRequester).focusGroup(),
        buttonModifier = { index ->
            Modifier.focusRequester(requesters[index]).focusProperties {
                left = (index - 1 downTo 0).firstOrNull { buttons[it].enabled }
                    ?.let { requesters[it] } ?: FocusRequester.Cancel
                right = (index + 1 until buttons.size).firstOrNull { buttons[it].enabled }
                    ?.let { requesters[it] } ?: FocusRequester.Cancel
                up = upFocusRequester
                down = downFocusRequester
            }
        },
    )
    LaunchedEffect(firstEnabled) {
        if (!initialFocusRequested) {
            inputModeManager.requestInputMode(InputMode.Keyboard)
            if (firstEnabled >= 0) {
                requesters[firstEnabled].requestFocus()
            } else if (downFocusRequester != FocusRequester.Default) {
                // Mainline modules and restricted apps may have no enabled action buttons.
                downFocusRequester.requestFocus()
            }
            initialFocusRequested = true
        }
    }
}
