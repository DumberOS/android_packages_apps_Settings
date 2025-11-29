/*
 * Copyright (C) 2024 The Android Open Source Project
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

package com.android.settings.widget;

import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

/** Utility methods related to AppBarLayout behavior in Settings. */
public final class AppBarUtils {

    private AppBarUtils() {
        // No instances.
    }

    /**
     * Configures the {@link AppBarLayout} so that it always starts in a collapsed state and
     * remains non-expandable during scrolling interactions.
     */
    public static void configureAppBarLayout(@Nullable AppBarLayout appBarLayout) {
        if (appBarLayout == null) {
            return;
        }
        final ViewGroup.LayoutParams layoutParams = appBarLayout.getLayoutParams();
        if (layoutParams instanceof CoordinatorLayout.LayoutParams) {
            final CoordinatorLayout.LayoutParams coordinatorLayoutParams =
                    (CoordinatorLayout.LayoutParams) layoutParams;
            coordinatorLayoutParams.setBehavior(new NonExpandableAppBarBehavior());
            appBarLayout.setLayoutParams(coordinatorLayoutParams);
        }
        collapse(appBarLayout);
    }

    private static void collapse(AppBarLayout appBarLayout) {
        appBarLayout.setExpanded(false /* expanded */, false /* animate */);
        appBarLayout.post(() -> appBarLayout.setExpanded(false /* expanded */, false /* animate */));
    }
}
