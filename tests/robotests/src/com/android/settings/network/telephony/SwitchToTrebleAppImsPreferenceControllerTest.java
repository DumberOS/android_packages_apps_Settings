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

package com.android.settings.network.telephony;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import android.content.Context;
import android.os.SystemProperties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SwitchToTrebleAppImsPreferenceControllerTest {
    private static final String STOCK_IMS_PROPERTY = "sys.phh.stock_mtk_ims";

    private Context mContext;
    private SwitchToTrebleAppImsPreferenceController mController;

    @Before
    public void setUp() {
        // No subscription or telephony service is available from this context.
        mContext = mock(Context.class);
        mController = new SwitchToTrebleAppImsPreferenceController(
                mContext, "switch_to_treble_app_ims");
        clearInvocations(mContext);
    }

    @Test
    public void isAvailable_stockImsActive_doesNotRequireTelephony() {
        SystemProperties.set(STOCK_IMS_PROPERTY, "true");

        assertThat(mController.isAvailable()).isTrue();
        verifyNoMoreInteractions(mContext);
    }

    @Test
    public void isAvailable_defaultImsActive_hidden() {
        SystemProperties.set(STOCK_IMS_PROPERTY, "false");

        assertThat(mController.isAvailable()).isFalse();
        verifyNoMoreInteractions(mContext);
    }

    @Test
    public void isAvailable_propertyUnset_hidden() {
        SystemProperties.set(STOCK_IMS_PROPERTY, "");

        assertThat(mController.isAvailable()).isFalse();
        verifyNoMoreInteractions(mContext);
    }
}
