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

import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

/**
 * {@link AppBarLayout.Behavior} that prevents the app bar from expanding via nested scrolls or
 * drags, keeping the toolbar in its collapsed configuration.
 */
public class NonExpandableAppBarBehavior extends AppBarLayout.Behavior {

    public NonExpandableAppBarBehavior() {
        super();
        setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return false;
            }
        });
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout,
            @NonNull AppBarLayout child, @NonNull View target, int dx, int dy,
            @NonNull int[] consumed, int type) {
        if (dy > 0) {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        }
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout,
            @NonNull AppBarLayout child, @NonNull View target, int dx, int dy,
            @NonNull int[] consumed) {
        if (dy > 0) {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        }
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
            @NonNull AppBarLayout child, @NonNull View target, int dxConsumed, int dyConsumed,
            int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        if (dyUnconsumed >= 0) {
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed,
                    dxUnconsumed, dyUnconsumed, type, consumed);
        }
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
            @NonNull AppBarLayout child, @NonNull View target, int dxConsumed, int dyConsumed,
            int dxUnconsumed, int dyUnconsumed, int type) {
        if (dyUnconsumed >= 0) {
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed,
                    dxUnconsumed, dyUnconsumed, type);
        }
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
            @NonNull AppBarLayout child, @NonNull View target, int dxConsumed, int dyConsumed,
            int dxUnconsumed, int dyUnconsumed) {
        if (dyUnconsumed >= 0) {
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed,
                    dxUnconsumed, dyUnconsumed);
        }
    }

    @Override
    public boolean onNestedFling(@NonNull CoordinatorLayout coordinatorLayout,
            @NonNull AppBarLayout child, @NonNull View target, float velocityX, float velocityY,
            boolean consumed) {
        if (velocityY < 0) {
            return false;
        }
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY,
                consumed);
    }
}
